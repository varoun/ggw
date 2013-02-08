(ns ggw.health
  (:require [taoensso.carmine :as red])
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
    (red/with-conn pool connspec
      (red/ping))
    (catch java.net.SocketException e
      false)))

(defn all-ok? []
  (and (redis-up?)
       (client-thread-running?)))
