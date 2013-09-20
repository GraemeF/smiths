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

(defn rand-from-set
  ([m] (nth (seq m) (rand-int (count m))))
  ([m other]
   (let [c (count m)
         r (rand-int (+ 1 c))]
     (if (= c r)
       (other)
       (nth (seq m) r)))))

(defrecord InstanceProcess [instance nonce])
(defrecord Instance [application device])

(defn start-process [estate timestamp]
  (let [instance (rand-from-set (:instances estate))
        process (InstanceProcess. instance
                                  (rand-int Integer/MAX_VALUE))]
    (emit-event {:event-type "Process started"
                 :process process
                 :timestamp timestamp})
    (assoc estate 
           :processes (union (:processes estate) #{process}))))

(defn stop-process [estate timestamp]
  (let [process (rand-from-set (:processes estate))]
    (emit-event {:event-type "Process stopped"
                 :process process
                 :timestamp timestamp})
    (assoc estate 
           :processes (difference (:processes estate) #{process}))))

(defn add-instance [estate timestamp]
  (let [instance (Instance. (rand-from-set (:applications estate) generate-application)
                            (rand-from-set (:devices estate) generate-device))]
    (emit-event {:event-type "Instance added"
                 :instance instance
                 :timestamp timestamp})
    (assoc estate 
           :instances (union (:instances estate) #{instance})
           :applications (union (:applications estate) #{(:application instance)})
           :devices (union (:devices estate) #{(:device instance)}))))

(defn remove-instance [estate timestamp]
  (let [instance (rand-from-set (:instances estate))]
    (emit-event {:event-type "Instance removed"
                 :instance instance
                 :timestamp timestamp})
    (assoc estate :instances (difference (:instances estate) #{instance}))))

(def weighted-events
  {add-application-to-device 2
   remove-application-from-device 1
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
    (assoc changed-estate :event-count (+ 1 (:event-count changed-estate)))))

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
