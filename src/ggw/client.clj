(ns ggw.client
  (import [java.net Socket]
          [java.io PrintWriter])
  (:require [clj-redis.client :as redis])
  (:use [lamina.core]
        [aleph.tcp] 
        [gloss.core]
        [ggw.server]))

(defn make-graphite-channel 
  [g-host g-port]
  (wait-for-result
   (tcp-client {:host g-host 
                :port g-port
                :frame (string :utf-8 :delimiters ["\n"])})))

(defn send-metric-to-graphite 
  [host port metric-string]
  (with-open [socket (Socket. host port)
              out-str (.getOutputStream socket)]
    (binding [*out* (PrintWriter. out-str)]
      (println metric-string))))


(defn read-metric-from-db 
  [redis-db]
  (second (redis/brpop redis-db ["metric"] 0)))


(defn get-and-send-metric 
  [redis-db g-host g-port]
  (let [ch (make-graphite-channel g-host g-port)]
    (loop [metric (read-metric-from-db redis-db)]
      (enqueue ch metric)
      (recur (read-metric-from-db redis-db)))))
