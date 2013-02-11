(ns ggw.client
  (import [java.net Socket]
          [java.io PrintWriter])
  (:require [taoensso.carmine :as redis]
            [clojure.tools.logging :as log]
            [clojure.string :as string])
  (:use [ggw.redis]
        [ggw.conf]))

;; Atom to track the number of graphite requests
(def graphite-out (atom 0))

;; Talking to graphite
(defn send-metric-to-graphite 
  [host port metric-string]
  (try
    (with-open [socket (Socket. host port)
                out-str (.getOutputStream socket)]
      (binding [*out* (PrintWriter. out-str)]
        (println metric-string)))
    (catch java.net.ConnectException e
      (Thread/sleep 5000)
      (send-metric-to-graphite host port metric-string))))
             
    


(defn read-metric-from-db 
  [red-pool red-connspec]
  (string/join "\n" 
               (map second
                    (second (redis/with-conn red-pool red-connspec
                              (redis/brpop "metric" 0))))))


(defn get-and-send-metric 
  [red-pool red-connspec g-host g-port]
  (loop [metric (read-metric-from-db red-pool red-connspec)]
    (future (swap! graphite-out inc))
    (log/info "Sending to graphite" metric)
    (send-metric-to-graphite g-host g-port metric)
    (recur (read-metric-from-db red-pool red-connspec))))

(defmacro start-client [red-pool red-connspec g-host g-port]
  `(def client 
     (future (get-and-send-metric ~red-pool ~red-connspec ~g-host ~g-port))))

;;; start the client
;(start-client pool connspec graphite-host graphite-port)
