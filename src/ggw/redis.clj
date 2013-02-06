(ns ggw.redis
  (:require [clj-redis.client :as redis]))

;;; redis connection parameters
(def db (redis/init))

;;; redis health check
(defn redis-up? []
  (try
    (redis/ping db)
    (catch redis.clients.jedis.exceptions.JedisConnectionException e
      false)))
