(ns smiths.application
  (:require [clojure.data.generators :refer [string geometric printable-ascii-char weighted]]))

(defn geometric-sizer
  [mean-size]
  #(dec (geometric (/ 1 mean-size))))

(defrecord Application [manufacturer name version])

(defn generate-application []
  (Application. (string printable-ascii-char (geometric-sizer 40))
                (string printable-ascii-char (geometric-sizer 50))
                (string printable-ascii-char (geometric-sizer 8))))
