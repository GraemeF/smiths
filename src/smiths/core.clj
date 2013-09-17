(ns smiths.core
  (:require [clojure.pprint :refer [pprint]])
  (:require [clojure.set :refer [union difference]])
  (:require [clj-time.core :refer [minus now hours minutes]])
  (:require [clj-time.periodic :refer [periodic-seq]])
  (:require [smiths.device :refer [generate-device]])
  (:require [smiths.application :refer [generate-application]])
  (:require [clojure.data.generators :as gen])
  (:require [clojurewerkz.eep.emitter :refer [defobserver notify create]]))

(defn emit-event [event])
;(defn emit-event [event] (println (pprint event)))

(def interval-between-events (minutes 1))

(defn to-weird-map [m]
  {m nil})

(defn rand-from-map
  ([m] (nth (keys m) (rand-int (count m))))
  ([m other]
   (let [c (count m)
         r (rand-int (+ 1 c))]
     (if (= c r)
       (other)
       (rand-from-map m)))))

(defn add-application-to-device [estate timestamp]
  (let [instance {:application (rand-from-map (:applications estate) generate-application)
                  :device (rand-from-map (:devices estate) generate-device)}]
    (emit-event {:event-type "Application added"
                 :application (:application instance)
                 :device (:device instance)
                 :timestamp timestamp})
    (assoc estate 
           :instances (union (:instances estate) (to-weird-map instance))
           :applications (union (:applications estate) (to-weird-map (:application instance)))
           :devices (union (:devices estate) (to-weird-map (:device instance))))))

(defn remove-application-from-device [estate timestamp]
  (let [instance (rand-from-map (:instances estate))]
    (emit-event {:event-type "Application removed"
                 :application (:application instance)
                 :device (:device instance)
                 :timestamp timestamp})
    (let [result (assoc estate
                        :instances (dissoc (:instances estate) instance))]
      result)))

(def weighted-events
  {add-application-to-device 2
   remove-application-from-device 1})

(defn create-weighted-generator [entry estate timestamp]
  (first {#((key entry) estate timestamp) (val entry)}))

(defn print-stats [estate]
  (println (:event-count estate) "events comprising" 
           (count (:applications estate)) "applications," 
           (count (:devices estate)) "devices, and" 
           (count (:instances estate)) "instances.")
  estate)

(defn change-estate [estate timestamp]
  (when (= 0 (mod (:event-count estate) 10000))
    (print-stats estate))
  (let [changed-estate (gen/weighted (map #(create-weighted-generator % estate timestamp) 
                                          weighted-events))]
    (assoc changed-estate :event-count (+ 1 (:event-count changed-estate)))))

(def empty-estate {:event-count 0
                   :applications {}
                   :devices {}
                   :instances {}
                   :users {}})

(defn simulate-estate [start interval]
  (reduce change-estate empty-estate
          (take 50000 (periodic-seq start interval))))

(defn -main []
  (print-stats (time (simulate-estate (minus (now) (hours 1)) 
                                      interval-between-events))))
