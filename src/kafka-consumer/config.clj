(ns kafka-consumer.config
  (:require [environ.core :refer [env]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            ))

(def environment (or (env :environment) "development"))

(def config
  (let [config-file-name (str "config-" environment ".edn")
        file-config (or
                      (try
                        (-> config-file-name
                            io/resource
                            slurp
                            edn/read-string)
                        (catch Exception e
                          (error (str "Failed to read configuration file:" config-file-name))
                          (throw e)))
                      {})
        env-config (select-keys env (keys file-config))
        ]
    (merge file-config env-config)))

(def kafka-consumer-config  (:kafka-consumer-config  config))

(info (str "Running in " environment " environment"))
