(ns cljssh.common
  (:import [java.util.concurrent TimeUnit]))

(def property-file ".temp/properties.clj")

(def default-timeout-seconds 2)
(def default-timeout-milseconds
  (.toMillis TimeUnit/SECONDS default-timeout-seconds))
