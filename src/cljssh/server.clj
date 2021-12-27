(ns cljssh.server
  (:require [cljssh.utils :as utils])
  (:import [org.apache.sshd.server SshServer]
           [java.nio.file LinkOption]
           [org.apache.sshd.server.keyprovider SimpleGeneratorHostKeyProvider]
           [org.apache.sshd.server.config.keys DefaultAuthorizedKeysAuthenticator]
           [org.apache.sshd.server.shell InteractiveProcessShellFactory]))

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

(defn run-server [server
                  {:keys [key-ser-file auth-keys-file port]}]
  (doto server
    (.setPort port)
    (.setKeyPairProvider
     (create-simple-generator-host-key-provider key-ser-file))
    (.setPublickeyAuthenticator
     (create-default-key-authentificator auth-keys-file))
    (.setShellFactory
     (InteractiveProcessShellFactory.))
    (.start)))

(defn stop-server [server]
  (.stop server))

(comment (def server (create-default-server)))
(comment (def server-attributes {:key-ser-file "key.ser"
                                 :auth-keys-file ".temp/testkeys/authorized_keys"
                                 :port 2222}))

(comment (run-server server server-attributes))
(comment (stop-server server))