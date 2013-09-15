(ns smiths.core
  (:require [clj-time.core :refer [minus now hours minutes]])
  (:require [clj-time.periodic :refer [periodic-seq]])
  (:require [smiths.application :refer [generate-application]])
  (:require [clojure.data.generators :as gen])
  (:require [clojurewerkz.eep.emitter :refer [defobserver notify create]]))

(defn emit-event [event]
  (println event))

(def interval-between-events (minutes 1))

(defn add-application-to-device [estate timestamp]
  (let [app (gen/one-of (conj (:applications estate) generate-application))
        device (gen/one-of (:devices estate))]
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
  {add-application-to-device 2
   generate-application-removed-event 1})

(defn create-weighted-generator [entry device timestamp]
  (first {#((key entry) device timestamp) (val entry)}))

(defn change-estate [estate timestamp]
  (gen/weighted (map #(create-weighted-generator %
                                                 estate
                                                 timestamp) 
                     weighted-events)))

(def empty-estate {:applications #{{:FOO "BAR!"}}
                   :devices #{{:id "foo"}}
                   :users #{}})

(defn simulate-estate [start interval]
  (reduce change-estate empty-estate
          (take 5 (periodic-seq start interval))))

(defn -main []
  (simulate-estate (minus (now) (hours 1)) 
                   interval-between-events))
