(ns ggw.server
  (:use compojure.core
        [ring.adapter.jetty :only (run-jetty)]
        [ring.middleware.params :only (wrap-params)])
  (:require [compojure.route :as route]
            [clj-redis.client :as redis]
            [clojure.string :as string]))

;;; redis connection parameters
(def db (redis/init))


;;; Writing the data to redis
(defn write-metrics 
  [metrics-map]
  (doseq [[k v] metrics-map]
    (redis/rpush db "metric" v)))


;;; http handlers
(defn metric-handler 
  [metrics-map]
  (try
    (do
      (write-metrics metrics-map)
      {:body "Done"})
    (catch redis.clients.jedis.exceptions.JedisConnectionException e
      {:body "Connection error!"})))


;;; Routes
(defroutes app*
  (POST "/v1/metrics" {params :form-params} (metric-handler params)))

;;; Ring middleware for handling form parameters
(def app (wrap-params app*))

;; ggw.server> (def server (run-jetty #'app {:port 8080 :join? false}))
;; #'ggw.server/server
;; ggw.server> 


