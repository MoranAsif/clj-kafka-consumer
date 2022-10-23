
(ns kafka-consumer.core
  (:require [kafka-consumer.config :as config]
            [kafka-consumer.server :as server]
            [kafka-consumer.kafka-consumer :refer [consumer-chan, consumer-source]]
            [kafka-consumer.server :refer [web-app-server]]
            [mount.core :refer [start with-args only]]
            )
  (:gen-class))


(defn web-handler-mode []
  (only #{
          #'consumer-chan
          #'consumer-source
          #'web-app-server
          }))


(defn -main
  [& args]
    (start (web-handler-mode) (with-args {:app-port config/config}))
    (server/consume_events consumer-chan)

    )



