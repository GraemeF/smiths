(ns smiths.core
  (:use [clj-time.core :only (minus now hours minutes)])
  (:use [clj-time.periodic :only (periodic-seq)]))

(def interval-between-events (minutes 1))

(defn generate-device-event [deviceId timestamp]
  {:event-type "Application installed"
   :application-name "Orca"
   :executables ["C:\\Program Files\\Orca\\Orca.exe"]
   :deviceId deviceId
   :timestamp timestamp})

(defn generate-device-events [start interval]
  (map #(generate-device-event "dev1" %) (periodic-seq start interval)))

(defn -main []
  (time (take 10 (generate-device-events (minus (now) (hours 1)) (minutes 15)))))
