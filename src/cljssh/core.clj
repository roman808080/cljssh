(ns cljssh.core
  (:require [cljssh.utils :as utils])

  (:import [org.apache.sshd.client SshClient]
           [org.apache.sshd.common.channel Channel]
           [org.apache.sshd.client.channel ClientChannelEvent]
           [org.apache.sshd.scp.client ScpClientCreator]
           [org.apache.sshd.common.util.security SecurityUtils]
           [org.apache.sshd.common.util.io.resource PathResource]
           [org.apache.sshd.common.config.keys FilePasswordProvider]

           [java.io ByteArrayOutputStream]
           [java.util.concurrent TimeUnit]
           [java.nio.file Files]))

(def property-file ".temp/properties.clj")

(def default-timeout-seconds 2)
(def default-timeout-milseconds
  (.toMillis TimeUnit/SECONDS default-timeout-seconds))

(defn start-client [client] (.start client))
(defn stop-client [client] (.stop client))

(defn create-session [client user host port] (-> client
                                                 (.connect user host port)
                                                 (.verify default-timeout-seconds
                                                          TimeUnit/SECONDS)
                                                 (.getSession)))

(defn add-password [session password]
  (doto session
    (.addPasswordIdentity password)))

(defn login [session]
  (-> session
      (.auth)
      (.verify default-timeout-seconds
               TimeUnit/SECONDS)))

(defn get-key [passphrase identity-file]
  (let [file-path (utils/path-object identity-file)]
    (with-open [input-stream (Files/newInputStream file-path [])]
      (-> (SecurityUtils/loadKeyPairIdentities
           nil (PathResource. file-path) input-stream
           (FilePasswordProvider/of passphrase))

          (.iterator)
          (.next)))))

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
          (.verify default-timeout-seconds TimeUnit/SECONDS))

      (with-open [pipe-in (.getInvertedIn channel)]
        (doto pipe-in
          (.write (.getBytes command))
          (.flush))
        (.waitFor channel
                  [ClientChannelEvent/CLOSED] default-timeout-milseconds)

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
                               user password
                               command
                               source destination]}]
  (try
    (let [client (. SshClient setUpDefaultClient)]
      (start-client client)

      (with-open [session (create-session client user host port)]
        (-> session
            (add-password password)
            (login))

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
  (-> (utils/load-edn property-file)
      (operate-on-connection)))

(comment (-main))
