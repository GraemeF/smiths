(ns smiths.core
  (:require [clojure.pprint :refer [pprint]])
  (:require [clojure.set :refer [union difference]])
  (:require [clj-time.core :refer [minus now hours minutes]])
  (:require [clj-time.periodic :refer [periodic-seq]])
  (:require [smiths.set :refer [rand-from-set]])
  (:require [smiths.event :refer [emit-event]])
  (:require [smiths.device :refer [generate-device]])
  (:require [smiths.application :refer [generate-application]])
  (:require [smiths.instance :refer [add-instance remove-instance]])
  (:require [smiths.process :refer [start-process stop-process]])
  (:require [clojure.data.generators :as gen])
  (:require [clojurewerkz.eep.emitter :refer [defobserver notify create]]))

(def interval-between-events (minutes 1))

(def weighted-events
  {add-instance 2
   remove-instance 1
   stop-process 3 
   start-process 4})

(defn create-weighted-generator [entry estate timestamp]
  (first {#((key entry) estate timestamp) (val entry)}))

(defn print-stats [estate]
  (println (:event-count estate) "events comprising" 
           (count (:applications estate)) "applications," 
           (count (:devices estate)) "devices, and" 
           (count (:instances estate)) "instances of which there are"
           (count (:processes estate)) "processes.")
  estate)

(defn change-estate [estate timestamp]
  (when (= 0 (mod (:event-count estate) 10000))
    (print-stats estate))
  (let [changed-estate (gen/weighted (map #(create-weighted-generator % estate timestamp) 
                                          weighted-events))]
    (assoc changed-estate :event-count (inc (:event-count changed-estate)))))

(def empty-estate {:event-count 0
                   :applications #{}
                   :devices #{}
                   :instances #{}
                   :processes #{}
                   :users #{}})

(defn simulate-estate
  ([qty]
   (simulate-estate (minus (now) (hours 1)) 
                    interval-between-events
                    qty))
  ([start interval qty]
   (reduce change-estate empty-estate
           (take qty (periodic-seq start interval)))))

(defn -main []
  (print-stats (time (simulate-estate (minus (now) (hours 1)) 
                                      interval-between-events
                                      100000))))
