(ns smiths.core
  (:use [clj-time.core :only (minus now hours minutes)])
  (:use [clj-time.periodic :only (periodic-seq)])
  (:use [clojure.data.generators :only (weighted)]))

(def interval-between-events (minutes 1))

(defn generate-application-added-event [device timestamp]
  {:event-type "Application added"
   :application-name "Orca"
   :executables ["C:\\Program Files\\Orca\\Orca.exe"]
   :deviceId (:id device)
   :timestamp timestamp})

(def weighted-events
  {generate-application-added-event 1})

(defn create-weighted-generator [entry device timestamp]
  (first {#((key entry) device timestamp) (val entry)}))

(defn generate-weighted-event [device timestamp]
  (weighted (map #(create-weighted-generator % device timestamp) weighted-events)))

(defn generate-device-events [start interval]
  (map #(generate-weighted-event {:id "dev1"} %) (periodic-seq start interval)))

(defn -main []
  (println (generate-device-events (minus (now) (hours 1)) interval-between-events)))
