(ns smiths.core-test
  (:require [midje.sweet :refer [facts fact]])
  (:require [smiths.core :refer :all]))

(facts "about `add-application-to-device`"
       (fact "it adds an application to the estate"
             (add-application-to-device {:devices #{} 
                                         :applications #{}
                                         :instances #{}} #inst "2000") 
             => #(= 1 (count (:applications %)))))

(facts "about `remove-application-from-device`"
       (fact "it removes an instance from the estate"
             (remove-application-from-device {:devices #{} 
                                              :applications #{}
                                              :instances #{{:foo "bar"}}} 
                                             #inst "2000") 
             => #(empty? (:instances %))))
