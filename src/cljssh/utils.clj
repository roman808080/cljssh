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

(defn join-paths [firt-path & other-paths]
  (Paths/get firt-path (into-array String other-paths)))

(defn path-object [str-path]
  (join-paths str-path))

(comment (load-edn ".temp/properties.clj"))
(comment (join-paths "first" "second" "third"))
(comment (path-object "string"))