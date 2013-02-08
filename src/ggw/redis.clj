(ns ggw.redis
  (:require [taoensso.carmine :as red]))

;;; redis connection parameters
(def pool (red/make-conn-pool))
(def connspec (red/make-conn-spec))
