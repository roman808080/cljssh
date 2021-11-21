(ns cljssh.utils
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.walk :as walk])
  (:import [java.util Properties]))

(defn convert-to-int [maybe-int]
  (try
    (Integer/parseInt maybe-int)
    (catch java.lang.NumberFormatException error nil)))

(defn convert-to-int-if [maybe-int]
  (if-let [converted-int (convert-to-int maybe-int)]
    converted-int
    maybe-int))

(defn load-properties [file-name]
  (with-open [reader (io/reader file-name)]
    (let [properties (Properties.)]
      (.load properties reader)
      (-> (into {} properties)
          (walk/keywordize-keys)))))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  (try
    (with-open [reader (io/reader source)]
      (edn/read (java.io.PushbackReader. reader)))

    (catch java.io.IOException e
      (printf "Couldn't open '%s': %s\n" source (.getMessage e)))
    (catch RuntimeException e
      (printf "Error parsing edn file '%s': %s\n" source (.getMessage e)))))

(comment (load-properties "test.properties"))
(comment (load-edn "properties.clj"))
(comment (convert-to-int "3.4"))
(comment (convert-to-int-if "34"))