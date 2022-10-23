(ns kafka-consumer.server
  (:require [kafka-consumer.config :as config]
            [mount.core :refer [defstate]]
            [org.httpkit.server :refer [run-server]]
            [compojure.core :refer :all]
            [clojure.data.json :as json]
            [cheshire.core :as c-json]
            [clojure.core.async :as async]
            [clj-http.client :as client]
            ))


(defn send-event-to-destination [destination-token event]
    (try
      (client/post
        config/destination-url
        {:headers
               {:destination-integration-key destination-token
                :content-type "application/json"}
         :body (c-json/generate-string event)
         :socket-timeout 5000
         :connection-timeout 5000
         })
      (catch Exception e
        (logger/error event (str  "Failed sending to " config/consumer-destination) e))))


(defn handle-send-event-to-destination  [event]
  (let [destination-token (get-destination destination-token)
        destination-res (send-event-to-destination destination-token event)]
    (if (= (:status destination-res) 200)
      (do logger/info "Event was sent to" config/consumer-destination event))
      (do logger/error "Event was not sent to" config/consumer-destination event))

(defn consume_events [in]
    (loop []
      (when-some [event (async/<!! in)]
        (info "one message consumed:" event)
          ((handle-send-event-to-destination event)
            (info "Event won't be sent to" config/consumer-destination event))
          (recur))))


(defroutes all-routes
           (GET "/health-check" [] "success"))

(defn start-web-app [app-port]
  (info "Starting web app at port" app-port)
  (run-server all-routes {:port app-port}))

(defn stop-web-app [server]
  (info "Shutting down web app")
  (server :timeout 1000))

(defstate web-app-server
          :start (start-web-app (:app-port config/config))
          :stop (stop-web-app web-app-server))