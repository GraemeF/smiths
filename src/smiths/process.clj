(ns smiths.process
  (:require [clojure.set :refer [union difference]])
  (:require [smiths.instance :refer :all])
  (:require [smiths.event :refer [emit-event]])
  (:require [smiths.set :refer [rand-from-set]]))

(defrecord InstanceProcess [instance nonce])

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
