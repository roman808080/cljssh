(ns cljssh.authentification
  (:require [cljssh.utils :as utils]
            [cljssh.common :as common])

  (:import [org.apache.sshd.common.util.security SecurityUtils]
           [org.apache.sshd.common.util.io.resource PathResource]
           [org.apache.sshd.common.config.keys FilePasswordProvider]

           [java.util.concurrent TimeUnit]
           [java.nio.file Files OpenOption]))

(defn login [session]
  (-> session
      (.auth)
      (.verify common/default-timeout-seconds
               TimeUnit/SECONDS)))

(defn empty-option-array []
  (into-array OpenOption nil))

(defn load-key-pair-identities [file-path input-stream passphrase]
  (SecurityUtils/loadKeyPairIdentities nil (PathResource. file-path) input-stream
                                       (FilePasswordProvider/of passphrase)))

(defn get-key [passphrase identity-file]
  (let [file-path (utils/path-object identity-file)]
    (with-open [input-stream (Files/newInputStream file-path (empty-option-array))]

      (-> (load-key-pair-identities file-path input-stream passphrase)
          (.iterator)
          (.next)))))

(defn add-authentification-identities [session
                                       {:keys [password passphrase identity-file]}]
  (when password
    (.addPasswordIdentity session password))

  ;; TODO: Adding handling of a situation when we have the identity file, but do not have passphrase.
  (when (and passphrase identity-file)
    (.addPublicKeyIdentity session
                           (get-key passphrase identity-file)))

  (when (every? nil? [password passphrase identity-file])
    (throw (ex-info
            "Does not have any identities"
            {})))

  session)