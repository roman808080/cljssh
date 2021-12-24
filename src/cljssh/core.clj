(ns cljssh.core
  (:require [cljssh.utils :as utils]
            [cljssh.common :as common]
            [cljssh.authentification :as auth])

  (:import [org.apache.sshd.client SshClient]
           [org.apache.sshd.common.channel Channel]
           [org.apache.sshd.client.channel ClientChannelEvent]
           [org.apache.sshd.scp.client ScpClientCreator]

           [java.io ByteArrayOutputStream]
           [java.util.concurrent TimeUnit]))


(defn start-client [client] (.start client))
(defn stop-client [client] (.stop client))

(defn create-session [client user host port] (-> client
                                                 (.connect user host port)
                                                 (.verify common/default-timeout-seconds
                                                          TimeUnit/SECONDS)
                                                 (.getSession)))

(defn execute-command [session command]
  (try
    (with-open [response-stream (ByteArrayOutputStream.)
                error-stream (ByteArrayOutputStream.)
                channel (.createChannel session Channel/CHANNEL_EXEC command)]
      (doto channel
        (.setOut response-stream)
        (.setErr error-stream))

      (-> channel
          (.open)
          (.verify common/default-timeout-seconds TimeUnit/SECONDS))

      (with-open [pipe-in (.getInvertedIn channel)]
        (doto pipe-in
          (.write (.getBytes command))
          (.flush))
        (.waitFor channel
                  [ClientChannelEvent/CLOSED] common/default-timeout-milseconds)

        {:response (.toString response-stream)
         :error (.toString response-stream)}))

    (catch Exception exception
      {:response ""
       :error (.getMessage exception)})))

(defn upload-file [session source destination]
  (let [scp-client-creator (ScpClientCreator/instance)
        scp-client (.createScpClient scp-client-creator session)]
    (.upload scp-client
             source
             destination
             [])))

(defn operate-on-connection [{:keys
                              [host port
                               user
                               command
                               source destination] :as all-keys}]
  (try
    (let [client (. SshClient setUpDefaultClient)]
      (start-client client)

      (with-open [session (create-session client user host port)]
        (-> session
            (auth/add-authentification-identities all-keys)
            (auth/login))

        (let [{:keys [response error]}
              (execute-command session command)]

          (if (empty? error)
            (printf "The response:\n%s" response)
            (printf "The error:\n%s" error)))

        (upload-file session source destination))

      (stop-client client))

    (catch Exception exception
      (printf "Exception has happened on execute-command. Error = %s\n" (.getMessage exception)))))

(defn -main []
  (-> (utils/load-edn common/property-file)
      (operate-on-connection)))

(comment (-main))