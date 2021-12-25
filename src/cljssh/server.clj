(ns cljssh.server
  (:require [cljssh.utils :as utils])
  (:import [org.apache.sshd.server SshServer]
           [java.nio.file LinkOption]
           [org.apache.sshd.server.keyprovider SimpleGeneratorHostKeyProvider]
           [org.apache.sshd.server.config.keys DefaultAuthorizedKeysAuthenticator]))

(defn create-default-server []
  (SshServer/setUpDefaultServer))

(defn empty-link-option-array []
  (into-array LinkOption nil))

(defn create-default-key-authentificator [auth-keys-file]
  (-> (utils/path-object auth-keys-file)
      (DefaultAuthorizedKeysAuthenticator. false (empty-link-option-array))))

(defn run-server [server port]
  (doto server
    (.setPort port)
    (.setKeyPairProvider (SimpleGeneratorHostKeyProvider. (utils/path-object "key.ser")))
    (.setPublickeyAuthenticator
     (create-default-key-authentificator ".temp/testkeys/authorized_keys"))
    (.start)))

(defn stop-server [server]
  (.stop server))

(comment (def server (create-default-server)))
(comment (run-server server 2222))
(comment (stop-server server))
(comment (create-default-key-authentificator ".temp/testkeys/authorized_keys"))