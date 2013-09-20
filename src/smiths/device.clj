(ns smiths.device
  (:require [clojure.data.generators 
             :refer [string geometric printable-ascii-char weighted]]))

(defn geometric-sizer
  [mean-size]
  #(dec (geometric (/ 1 mean-size))))

(defrecord Device [id])

(defn generate-device []
  (Device. (string printable-ascii-char (geometric-sizer 10))))
