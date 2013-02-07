(ns ggw.health
  (:require [clj-redis.client :as red])
  (:use [ggw.client]
        [ggw.redis]))

;;; Client thread check

(defn client-thread-running? []
  (try
    (deref client 200 true)
    (catch java.lang.ClassCastException e false)))

;;; redis health check
(defn redis-up? []
  (try
    (red/ping db)
    (catch redis.clients.jedis.exceptions.JedisConnectionException e
      false)))

(defn all-ok? []
  (and (redis-up?)
       (client-thread-running?)))
