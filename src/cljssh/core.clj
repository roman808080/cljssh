(ns cljssh.core
  (:require [cljssh.utils :as utils])
  (:import [org.apache.sshd.client SshClient]))

(def client (. SshClient setUpDefaultClient))
(def property-file ".temp/properties.clj")

(defn start-client [] (.start client))
(defn stop-client [] (.stop client))

(defn create-session [user, host, port] (-> client
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

(defn establish-connection [{:keys [host port user password]}]
  (with-open [session (create-session user host port)]
    (-> session
        (add-password password)
        (login))))

(defn -main []
  (start-client)
  (-> (utils/load-edn property-file)
      (establish-connection))
  (stop-client))


(comment (with-open [session (create-session "roman" "localhost" 22)]
           (-> session
               (add-password "")
               (login))))

(comment (-main))