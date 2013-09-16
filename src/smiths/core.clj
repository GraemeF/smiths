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
  ;;(println (pprint event)))
  )

(def interval-between-events (minutes 1))

(defn add-application-to-device [estate timestamp]
  (println (class (:applications estate)))
  (let [instance {:application (rand-nth (keys (conj (:applications estate) (generate-application))))
                  :device (rand-nth (keys (conj (:devices estate) (generate-device))))}]
    (emit-event {:event-type "Application added"
                 :application (:application instance)
                 :device (:device instance)
                 :timestamp timestamp})
    (assoc estate 
           :instances (union (:instances estate) {instance instance})
           :applications (union (:applications estate) {(:application instance)(:application instance)})
           :devices (union (:devices estate) {(:device instance)(:device instance)}))))

(defn remove-application-from-device [estate timestamp]
  (println "Estate" estate timestamp)
  (println "Instances" (:instances estate))
  (let [instance (rand-nth (keys (:instances estate)))]
    (emit-event {:event-type "Application removed"
                 :application (:application instance)
                 :deviceId (:device instance)
                 :timestamp timestamp})
    (println "Removing" instance "from" estate)
    (let [result (assoc estate
                        :instances (dissoc (:instances estate) instance))]
      (pprint result)
      result)))

(def weighted-events
  {add-application-to-device 2
   remove-application-from-device 1})

(defn create-weighted-generator [entry estate timestamp]
  (first {#((key entry) estate timestamp) (val entry)}))

(defn print-stats [estate]
  (println (count (:applications estate)) "applications," (count (:devices estate)) "devices, and" (count (:instances estate)) "instances."))

(defn change-estate [estate timestamp]
  (print-stats estate)
  (gen/weighted (map #(create-weighted-generator %
                                                 estate
                                                 timestamp) 
                     weighted-events)))

(def empty-estate {:applications {}
                   :devices {}
                   :instances {}
                   :users {}})

(defn simulate-estate [start interval]
  (reduce change-estate empty-estate
          (take 500 (periodic-seq start interval))))

(defn -main []
  (print-stats (simulate-estate (minus (now) (hours 1)) 
                                interval-between-events)))
