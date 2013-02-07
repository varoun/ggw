(ns ggw.client
  (import [java.net Socket]
          [java.io PrintWriter])
  (:require [clj-redis.client :as redis]
            [clojure.tools.logging :as log])
  (:use [lamina.core]
        [aleph.tcp] 
        [gloss.core]
        [ggw.redis]
        [ggw.conf]))

(defn make-graphite-channel 
  [g-host g-port]
  (wait-for-result
   (tcp-client {:host g-host 
                :port g-port
                :frame (string :utf-8 :delimiters ["\n"])})))


(defn read-metric-from-db 
  [redis-db]
  (second (redis/brpop redis-db ["metric"] 0)))


(defn get-and-send-metric 
  [redis-db g-host g-port]
  (let [ch (make-graphite-channel g-host g-port)]
    (loop [metric (read-metric-from-db redis-db)]
      (when (not (closed? ch))
        (log/info "Sending to graphite" metric)
        (enqueue ch metric)
        (recur (read-metric-from-db redis-db))))))

(defmacro start-client [redis-db g-host g-port]
  `(def client 
     (future (get-and-send-metric ~redis-db ~g-host ~g-port))))
