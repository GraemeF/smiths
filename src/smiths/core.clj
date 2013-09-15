(ns smiths.core
  (:require [clojure.pprint :refer [pprint]])
  (:require [clojure.set :refer [union difference]])
  (:require [clj-time.core :refer [minus now hours minutes]])
  (:require [clj-time.periodic :refer [periodic-seq]])
  (:require [smiths.device :refer [generate-device]])
  (:require [smiths.application :refer [generate-application]])
  (:require [clojure.data.generators :as gen])
  (:require [clojurewerkz.eep.emitter :refer [defobserver notify create]]))

(defn emit-event [event]
  (println (pprint event)))

(def interval-between-events (minutes 1))

(defn add-application-to-device [estate timestamp]
  (let [instance {:application (gen/one-of (union (:applications estate) #{(generate-application)}))
                  :device (gen/one-of (union (:devices estate) (generate-device)))}]
    (emit-event {:event-type "Application added"
                 :application (:application instance)
                 :device (:device instance)
                 :timestamp timestamp})
    (println "ADDING" instance)
    (assoc estate 
           :instances (union (:instances estate) #{instance})
           :applications (union (:applications estate) #{(:application instance)})
           :devices (union (:devices estate) #{(:device instance)}))))

(defn remove-application-from-device [estate timestamp]
  (let [instance (gen/one-of (:instances estate))]
    (emit-event {:event-type "Application removed"
                 :application (:application instance)
                 :deviceId (:device instance)
                 :timestamp timestamp})
    (assoc estate
           :instances (disj (:instances estate) instance))))

(def weighted-events
  {add-application-to-device 2
   remove-application-from-device 1})

(defn create-weighted-generator [entry estate timestamp]
  (first {#((key entry) estate timestamp) (val entry)}))

(defn print-stats [estate]
  (println (count (:applications estate)) "applications," (count (:devices estate)) "devices, and" (count (:instances estate)) "instances." \newline))

(defn change-estate [estate timestamp]
  (println \newline \newline "Estate:" (pprint estate))
  (print-stats estate)
  (gen/weighted (map #(create-weighted-generator %
                                                 estate
                                                 timestamp) 
                     weighted-events)))

(def empty-estate {:applications #{}
                   :devices #{}
                   :instances #{}
                   :users #{}})

(defn simulate-estate [start interval]
  (reduce change-estate empty-estate
          (take 5 (periodic-seq start interval))))

(defn -main []
  (println (str "ESTATE:" (simulate-estate (minus (now) (hours 1)) 
                                           interval-between-events))))
