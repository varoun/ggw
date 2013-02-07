(ns ggw.core
  (:use [ggw.conf]
        [ggw.redis]
        [ggw.client]
        [ggw.server]
        [ggw.health]))


(defn -main []
  (all-ok?))
