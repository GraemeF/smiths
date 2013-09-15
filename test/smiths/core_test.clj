(ns smiths.core-test
  (:use midje.sweet)
  (:require [smiths.core :refer :all]))

(facts "about `generate-application-added-event`"
  (fact "it adds an application to the device"
    (generate-application-added-event {:applications #{}} #inst "2000") 
        => #(= 1 (count (:applications %)))))
