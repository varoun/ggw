(ns ggw.core
  (:use [ggw.redis]
        [ggw.server]
        [ggw.client]
        [ring.adapter.jetty :only (run-jetty)]))
        



(defn -main []
  (start-server http-port)
  (start-client db graphite-host graphite-port))
  
