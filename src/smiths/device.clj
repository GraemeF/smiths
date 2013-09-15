(ns smiths.device
  (:require [clojure.data.generators 
             :refer [string geometric printable-ascii-char weighted]]))

(defn geometric-sizer
  [mean-size]
  #(dec (geometric (/ 1 mean-size))))

(defn generate-device []
  {:id (string printable-ascii-char (geometric-sizer 10))})
