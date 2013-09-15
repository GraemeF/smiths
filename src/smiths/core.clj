(ns smiths.core
  (:use [clj-time.core :only (minus now hours minutes)])
  (:use [clj-time.periodic :only (periodic-seq)])
  (:use [smiths.application :only (generate-application)])
  (:use [clojure.data.generators :only (weighted)])
  (:use [clojurewerkz.eep.emitter :only (defobserver notify create)]))

(defn emit-event [event]
  (println event))

(def interval-between-events (minutes 1))

(defn generate-application-added-event [device timestamp]
  (let [app (generate-application)]
    (emit-event {:event-type "Application added"
                 :application app
                 :deviceId (:id device)
                 :timestamp timestamp})
    (assoc device :applications (conj (:applications device) app))))

(defn generate-application-removed-event [device timestamp]
  (emit-event {:event-type "Application removed"
               :application (generate-application)
               :deviceId (:id device)
               :timestamp timestamp})
  device)

(def weighted-events
  {generate-application-added-event 2
   generate-application-removed-event 1})

(defn create-weighted-generator [entry device timestamp]
  (first {#((key entry) device timestamp) (val entry)}))

(defn generate-weighted-event [device timestamp]
  (weighted (map #(create-weighted-generator % device timestamp) weighted-events)))

(defn simulate-estate [start interval]
  (map #(generate-weighted-event {:id "dev1"} %) (periodic-seq start interval)))

(defn -main []
  (dorun (simulate-estate (minus (now) (hours 1)) 
                          interval-between-events)))
