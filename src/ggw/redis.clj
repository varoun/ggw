(ns ggw.redis
  (:require [clj-redis.client :as red]))

;;; redis connection parameters
(def db (red/init))

