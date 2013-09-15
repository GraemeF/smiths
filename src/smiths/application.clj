(ns smiths.application
  (:use [clojure.data.generators :only (string geometric printable-ascii-char weighted)]))

(defn geometric-sizer
  [mean-size]
  #(dec (geometric (/ 1 mean-size))))

(defn generate-application []
  {:manufacturer (string printable-ascii-char (geometric-sizer 40))
   :name (string printable-ascii-char (geometric-sizer 50))
   :version (string printable-ascii-char (geometric-sizer 8))})
