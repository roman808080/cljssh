(ns cljssh.core
  (:require [cljssh.utils :as utils])
  (:import [org.apache.sshd.client SshClient]
           [org.apache.sshd.common.channel Channel]
           [java.io ByteArrayOutputStream]))

(def property-file ".temp/properties.clj")

(defn start-client [client] (.start client))
(defn stop-client [client] (.stop client))

(defn create-session [client user host port] (-> client
                                                 (.connect user host port)
                                                 (.verify 100)
                                                 (.getSession)))

(defn add-password [session password]
  (doto session
    (.addPasswordIdentity password)))

(defn login [session]
  (-> session
      (.auth)
      (.verify 1000)))

(defn execute-command [session command]
  (with-open [response-stream (ByteArrayOutputStream.)
              error-stream (ByteArrayOutputStream.)
              channel (.createChannel session Channel/CHANNEL_SHELL command)]
    (doto channel
      (.setOut response-stream)
      (.setErr error-stream)
      )
    (-> channel
        (.open)
        (.verify 1000))))

(defn operate-on-connection [{:keys [host port user password]}]
  (let [client (. SshClient setUpDefaultClient)]
    (start-client client)

    (with-open [session (create-session client user host port)]
      (-> session
          (add-password password)
          (login))
      (execute-command session "dir"))

    (stop-client client)))

(defn -main []
  (-> (utils/load-edn property-file)
      (operate-on-connection)))

(comment (-main))