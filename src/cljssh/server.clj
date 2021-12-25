(ns cljssh.server
  (:require [cljssh.utils :as utils])
  (:import [org.apache.sshd.server SshServer]
           [org.apache.sshd.server.keyprovider SimpleGeneratorHostKeyProvider]))

(defn run-server [port]
  (doto (SshServer/setUpDefaultServer)
    (.setPort port)
    (.setKeyPairProvider (SimpleGeneratorHostKeyProvider. (utils/path-object "key.ser")))
    (.start)))

(comment (run-server 2222))
(comment (SimpleGeneratorHostKeyProvider. (utils/path-object "key.ser")))
(comment (utils/path-object "key.ser"))