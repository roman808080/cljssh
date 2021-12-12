(ns cljssh.utils
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import [java.nio.file Paths]))

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

(defn path-join [p & ps]
  (str (.normalize (java.nio.file.Paths/get p (into-array String ps)))))

(comment (load-edn ".temp/properties.clj"))
(comment (Paths/get "my/complicated/path" (into-array String ["hell"])))
(comment (path-join "my/complicated/path" "hell"))
