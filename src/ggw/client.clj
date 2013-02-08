(ns ggw.client
  (import [java.net Socket]
          [java.io PrintWriter])
  (:require [taoensso.carmine :as redis]
            [clojure.tools.logging :as log]
            [clojure.string :as string])
  (:use [lamina.core]
        [aleph.tcp] 
        [gloss.core]
        [ggw.redis]
        [ggw.conf]))

;; Atom to track the number of graphite requests
(def graphite-out (atom 0))

;; Talking to graphite
(defn make-graphite-channel 
  [g-host g-port]
  (wait-for-result
   (tcp-client {:host g-host 
                :port g-port
                :frame (string :utf-8 :delimiters ["\n"])})))


(defn read-metric-from-db 
  [red-pool red-connspec]
  (string/join "\n" 
               (map second
                    (second (redis/with-conn red-pool red-connspec
                              (redis/brpop "metric" 0))))))


(defn get-and-send-metric 
  [red-pool red-connspec g-host g-port]
  (let [ch (make-graphite-channel g-host g-port)]
    (loop [metric (read-metric-from-db red-pool red-connspec)]
      (when (not (closed? ch))
        (future (swap! graphite-out inc))
        (log/info "Sending to graphite" metric)
        (enqueue ch metric)
        (recur (read-metric-from-db red-pool red-connspec))))))

(defmacro start-client [red-pool red-connspec g-host g-port]
  `(def client 
     (future (get-and-send-metric ~red-pool ~red-connspec ~g-host ~g-port))))

;;; start the client
;(start-client pool connspec graphite-host graphite-port)
