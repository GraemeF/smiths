(ns smiths.instance
  (:require [clojure.set :refer [union difference]])
  (:require [smiths.set :refer [rand-from-set]])
  (:require [smiths.device :refer [generate-device]])
  (:require [smiths.application :refer [generate-application]])
  (:require [smiths.event :refer [emit-event]])
  (:require [clojure.data.generators :as gen]))

(defrecord Instance [application device])

(defn add-instance [estate timestamp]
  (let [instance (Instance. (rand-from-set (:applications estate) generate-application)
                            (rand-from-set (:devices estate) generate-device))]
    (if-not (contains? (:instances estate) instance)
      (do (emit-event {:event-type "Instance added"
                       :instance instance
                       :timestamp timestamp})
          (assoc estate 
                 :instances (union (:instances estate) #{instance})
                 :applications (union (:applications estate) #{(:application instance)})
                 :devices (union (:devices estate) #{(:device instance)})))
      (recur estate timestamp))))

(defn remove-instance [estate timestamp]
  (let [instance (rand-from-set (:instances estate))]
    (emit-event {:event-type "Instance removed"
                 :instance instance
                 :timestamp timestamp})
    (assoc estate :instances (difference (:instances estate) #{instance}))))
