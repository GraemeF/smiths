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

(defn change-estate [estate timestamp]
  (weighted (map #(create-weighted-generator % (first (:devices estate)) timestamp) weighted-events)))

(def empty-estate {:applications #{}
                   :devices #{{:id "foo"}}
                   :users #{}})

(defn simulate-estate [start interval]
  (reduce change-estate empty-estate
          (periodic-seq start interval)))

(defn -main []
  (simulate-estate (minus (now) (hours 1)) 
                   interval-between-events))
