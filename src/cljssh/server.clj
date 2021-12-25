(ns cljssh.server
  (:require [cljssh.utils :as utils])
  (:import [org.apache.sshd.server SshServer]
           [org.apache.sshd.server.keyprovider SimpleGeneratorHostKeyProvider]))

(defn create-default-server []
  (SshServer/setUpDefaultServer))

(defn run-server [server port]
  (doto server
    (.setPort port)
    (.setKeyPairProvider (SimpleGeneratorHostKeyProvider. (utils/path-object "key.ser")))
    (.start)))

(defn stop-server [server]
  (.stop server))

(comment (def server (create-default-server)))
(comment (run-server server 2222))
(comment (stop-server server))
