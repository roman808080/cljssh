(ns cljssh.utils
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import [java.nio.file Path Paths]))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  (try
    (with-open [reader (io/reader source)]
      (edn/read (java.io.PushbackReader. reader)))

    (catch java.io.IOException e
      (printf "Couldn't open '%s': %s\n" source (.getMessage e))
      {})
    (catch RuntimeException e
      (printf "Error parsing edn file '%s': %s\n" source (.getMessage e))
      {})))

(defn get-str-array [& strings-to-convert]
  (into-array String strings-to-convert))

(defn path-join [p & ps]
  (str (Paths/get p (apply get-str-array ps))))

(defn get-path-object [str-path]
  (Path/of str-path (get-str-array)))

(comment (load-edn ".temp/properties.clj"))
(comment (Paths/get "my/complicated/path" (into-array String ["hell"])))
(comment (path-join "my/complicated/path" "hell"))
(comment (get-path-object "/path/to/my/strange/path"))
(comment (into-array String [nil]))
(comment (into-array String []))
(comment (get-str-array "hell"))
(comment (get-str-array))
(comment (type (get-str-array)))

(comment (class (to-array '(0))))
(comment (make-array String 4))
(comment (cast Number 1))

(comment (into-array String ""))
(comment (into-array Number [1 2 3]))
(comment (into-array Number nil))
(comment (into-array Number '()))
(comment (into-array Number '(1 2 3 4)))

(def array-value (into-array Number '(1 2 3 4)))
(comment (first array-value))
(comment (second array-value))

(def second-array (amap array-value
                        idx
                        ret
                        (+ (int 1)
                           (aget array-value idx))))

(comment (aget second-array 0))

(defn empty-function [& something]
  something)

(defn empty-function-convert-to-array [& something]
  (into-array Number something))

(comment (str (Paths/get "my/complicated/path" (into-array String ["hell"]))))
(comment (str (Paths/get "my/complicated/path" (into-array String []))))
(comment (str (Paths/get "my/complicated/path" (into-array String nil))))
(comment (str (Paths/get "my/complicated/path" (into-array String (empty-function)))))
(comment (str (Paths/get "my/complicated/path" (empty-function-convert-to-array "hell"))))

(comment (str (Paths/get "my/complicated/path" (get-str-array "hell"))))
(comment (str (Paths/get "my/complicated/path" (apply get-str-array '("hell")))))
(comment (str (Paths/get "my/complicated/path" (apply get-str-array '("step" "another" "hell")))))

(comment (path-join "my/complicated/path" "hell" "another"))

(comment (empty-function))
(comment (empty-function (empty-function)))
(comment (apply empty-function (empty-function)))

(comment (empty-function 4))
(comment (empty-function (empty-function 4)))
(comment (apply empty-function (empty-function 4)))

(comment (empty-function 2 3 4))
(comment (empty-function (empty-function 2 3 4)))
(comment (apply empty-function (empty-function 2 3 4)))

(comment (apply empty-function-convert-to-array '(2 3 4)))

(comment (empty-function nil))
(comment (get-path-object "my/path"))