(ns ggw.server
  (:use compojure.core
        [ring.adapter.jetty :only (run-jetty)]
        [ring.middleware.params :only (wrap-params)]
        [clojure.tools.logging :only (info error)]
        [ggw.redis]
        [ggw.conf]
        [ggw.health]
        [ggw.client])
  (:require [compojure.route :as route]
            [taoensso.carmine :as red]
            [clojure.string :as string]))

;; Atom to track the number of http requests
(def http-in (atom 0))

;;; Writing the data to redis
;; (defmacro write-metrics 
;;   [metrics-map]
;;   `(red/with-conn pool connspec
;;      (red/rpush "metric" ~@(map second metrics-map))))


;;; http handlers
(defn metric-handler 
  [metrics-map]
  (try
    (do
      (future (swap! http-in inc))
      (info metrics-map)
      `(red/with-conn pool connspec
         (red/rpush "metric" ~@(map second metrics-map)))
      {:status 201})
    (catch java.net.SocketException e
      (do
        (error "Writing to Redis failed - not running?")
        {:status 500}))))

(defn health-check []
  (if (all-ok?)
    {:status 200}
    {:status 500}))

(defn app-metrics []
  {:status 200
   :body (format "http-in=%s|graphite-out=%s\n" 
                 (deref http-in) (deref graphite-out))})

;;; Routes
(defroutes app*
  (POST "/v1/metrics" {params :form-params} (metric-handler params))
  (GET "/status" req (health-check))
  (GET "/appmetrics" req (app-metrics)))

;;; Ring middleware for handling form parameters
(def app (wrap-params app*))

;; ggw.server> (def server (run-jetty #'app {:port 8080 :join? false}))
;; #'ggw.server/server
;; ggw.server> 

 
(defmacro start-server 
  [port]
  `(def server
     (run-jetty #'app {:port ~port :join? false})))

;; Start the server 
;(start-server http-port)
