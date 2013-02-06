(ns ggw.redis
  (:require [clj-redis.client :as red]))

;;; redis connection parameters
(def db (red/init))

;;; redis health check
(defn redis-up? []
  (try
    (red/ping db)
    (catch redis.clients.jedis.exceptions.JedisConnectionException e
      false)))
