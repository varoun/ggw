(ns ggw.redis
  (:require [clj-redis.client :as redis]))

;;; redis connection parameters
(def db (redis/init))
