(ns smiths.process-test
  (:require [midje.sweet :refer [facts fact]])
  (:require [smiths.process :refer [start-process]]))

(facts "about starting a process"
       (fact "it adds a process to the instance"
             (start-process empty-estate #inst "2000")
             => #(= 1 (count (:processes %)))))
