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

(defn create-simple-generator-host-key-provider [key-ser-file]
  (SimpleGeneratorHostKeyProvider.
   (utils/path-object key-ser-file)))

(defn run-server [server key-ser-file auth-keys-file port]
  (doto server
    (.setPort port)
    (.setKeyPairProvider
     (create-simple-generator-host-key-provider key-ser-file))
    (.setPublickeyAuthenticator
     (create-default-key-authentificator auth-keys-file))
    (.start)))

(defn stop-server [server]
  (.stop server))

(comment (def server (create-default-server)))
(comment (run-server server "key.ser" ".temp/testkeys/authorized_keys" 2222))
(comment (stop-server server))
(comment (create-default-key-authentificator ".temp/testkeys/authorized_keys"))