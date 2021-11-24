(ns cljssh.core
  (:require [cljssh.utils :as utils])
  (:import [org.apache.sshd.client SshClient]))

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

(defn operate-on-connection [{:keys [host port user password]}]
  (let [client (. SshClient setUpDefaultClient)]
    (start-client client)

     (with-open [session (create-session client user host port)]
       (-> session
           (add-password password)
           (login)))

     (stop-client client)))

(defn -main []
  (-> (utils/load-edn property-file)
      (operate-on-connection)))

(comment (-main))