(ns smiths.core
  (:use [clj-time.core :only (minus now hours minutes)])
  (:use [clj-time.periodic :only (periodic-seq)]))

(def interval-between-events (minutes 1))

(defn generate-device-event [device timestamp]
  {:event-type "Application added"
   :application-name "Orca"
   :executables ["C:\\Program Files\\Orca\\Orca.exe"]
   :deviceId (:id device)
   :timestamp timestamp})

(defn generate-device-events [start interval]
  (map #(generate-device-event {:id "dev1"} %) (periodic-seq start interval)))

(defn -main []
  (println (generate-device-events (minus (now) (hours 1)) interval-between-events)))
