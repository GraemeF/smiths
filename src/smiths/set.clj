(ns smiths.set)

(defn rand-from-set
  ([m] (nth (seq m) (rand-int (count m))))
  ([m other]
   (let [c (count m)
         r (rand-int (+ 1 c))]
     (if (= c r)
       (other)
       (nth (seq m) r)))))
