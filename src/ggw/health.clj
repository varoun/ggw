(ns ggw.health
  (:require [clj-redis.client :as redis])
  (:use [ggw.core]))

(defn client-thread-running? []
  (try
    (deref client 200 true)
    (catch java.lang.ClassCastException e false)))

(defn all-ok? []
  (and (redis-up?)
       (client-thread-running?)))