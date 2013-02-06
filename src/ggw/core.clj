(ns ggw.core
  (:use [ggw.server]
        [ggw.client]
        [ring.adapter.jetty :only (run-jetty)]))
        

;;; Configs
(def graphite-host "localhost")
(def graphite-port 9090)
(def http-port 8080)

(defmacro start-server 
  [port]
  `(def server
     (run-jetty #'app {:port ~port :join? false})))

(defmacro start-client [redis-db g-host g-port]
  `(def client 
     (future (get-and-send-metric ~redis-db ~g-host ~g-port))))


(defn -main []
  (start-server http-port)
  (start-client db graphite-host graphite-port))
  
