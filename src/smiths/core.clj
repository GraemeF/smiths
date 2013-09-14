(ns smiths.core
  (:use [clj-time.core :only (minus now hours minutes)])
  (:use [clj-time.periodic :only (periodic-seq)])
  (:use [clojure.data.generators :only (string geometric printable-ascii-char weighted)]))

(def interval-between-events (minutes 1))

(defn geometric-sizer
  [mean-size]
  #(dec (geometric (/ 1 mean-size))))

(defn generate-application []
  {:manufacturer (string printable-ascii-char (geometric-sizer 40))
   :name (string printable-ascii-char (geometric-sizer 50))
   :version (string printable-ascii-char (geometric-sizer 8))})

(defn generate-application-added-event [device timestamp]
  {:event-type "Application added"
   :application (generate-application)
   :deviceId (:id device)
   :timestamp timestamp})

(defn generate-application-removed-event [device timestamp]
  {:event-type "Application removed"
   :application (generate-application)
   :deviceId (:id device)
   :timestamp timestamp})

(def weighted-events
  {generate-application-added-event 2
   generate-application-removed-event 1})

(defn create-weighted-generator [entry device timestamp]
  (first {#((key entry) device timestamp) (val entry)}))

(defn generate-weighted-event [device timestamp]
  (weighted (map #(create-weighted-generator % device timestamp) weighted-events)))

(defn generate-device-events [start interval]
  (map #(generate-weighted-event {:id "dev1"} %) (periodic-seq start interval)))

(defn -main []
  (println (map #(str % \newline)
                (generate-device-events (minus (now) (hours 1)) 
                                        interval-between-events))))
