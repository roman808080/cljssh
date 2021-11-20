(ns cljssh.core
  (:import [org.apache.sshd.client SshClient]))

(def client (. SshClient setUpDefaultClient))

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

;; (defn login-with)

(comment (start-client))
(comment (create-session "roman" "localhost" 22))
(comment (-> (create-session "roman" "localhost" 22)
             (add-password "")
             (login)))

(comment (stop-client))
