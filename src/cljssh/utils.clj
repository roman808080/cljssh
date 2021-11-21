(ns cljssh.utils
  (:require [clojure.java.io])
  (:import [java.util Properties]))

(defn load-properties [file-name]
  (with-open [^java.io.Reader reader (clojure.java.io/reader file-name)]
    (let [properties (Properties.)]
      (.load properties reader)
      (into {} properties))))

;; (defn convert-port-to-number [properties] (assoc 
;; ))

(comment (load-properties "test.properties"))