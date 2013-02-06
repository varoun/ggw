(ns ggw.health
  (:require [clj-redis.client :as redis])
  (:use [ggw.core]
        [ggw.redis]))

(defn redis-up? []
  (try
    (redis/ping db)
    (catch redis.clients.jedis.exceptions.JedisConnectionException e
      false)))

(defn client-thread-running? []
  (try
    (deref client 200 true)
    (catch java.lang.ClassCastException e false)))

(defn all-ok? []
  (and (redis-up?)
       (client-thread-running?)))