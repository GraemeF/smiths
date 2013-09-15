(ns smiths.core-test
  (:require [midje.sweet :refer [facts fact]])
  (:require [smiths.core :refer :all]))

(facts "about `add-application-to-device`"
       (fact "it adds an application to the device"
             (add-application-to-device {:applications #{}} #inst "2000") 
             => #(= 1 (count (:applications %)))))
