(ns cljssh.core
  (:require [cljssh.utils :as utils]
            [cljssh.common :as common]
            [cljssh.client :as client]))

(defn -main []
  (-> (utils/load-edn common/property-file)
      (client/operate-on-connection)))

(comment (-main))