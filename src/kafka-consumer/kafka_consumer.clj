(ns kafka-consumer.kafka-consumer
  (:require [cheshire.core :as json]
            [mount.core :refer [defstate]]
            [kafka-consumer.config :as config]
            [clojure.core.async :as async]
            ))

(defstate consumer-chan
          :start (async/chan 1000))

(defn run-kafka-consumer []
  (info "Starting kafka consumer")
  (let [consumer-config config/kafka-consumer-config]
    (info consumer-config)
    (consumer-source consumer-config consumer-chan)
    ))

(defstate consumer-source
          :start (run-kafka-consumer))

