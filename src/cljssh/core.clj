(ns cljssh.core
  (:import [org.apache.sshd.client SshClient]))

(def client (. SshClient setUpDefaultClient))

(defn start-client [] (.start client))
(defn stop-client [] (.stop client))

(defn create-session [user, host, port] (-> client
                                            (.connect user host port)
                                            (.verify 100)
                                            (.getSession)))

(comment (start-client))
(comment (create-session "roman" "localhost" 22))
(comment (stop-client))
