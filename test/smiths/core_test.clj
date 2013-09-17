(ns smiths.core-test
  (:require [midje.sweet :refer [facts fact every-checker contains has]])
  (:require [smiths.core :refer :all]))

(facts "about `add-application-to-device`"
       (let [estate 
             (add-application-to-device {:devices {} 
                                         :applications {}
                                         :instances {}} #inst "2000")]
         (fact "it adds an application to the estate"
               (count (:applications estate)) => 1)
         (fact "the added application looks like an application"
               (keys (first (keys (:applications estate)))) 
               => (contains #{:manufacturer
                              :name
                              :version}))))

(facts "about `remove-application-from-device`"
       (fact "it removes an instance from the estate"
             (let [instance {:foo "bar"}]
               (remove-application-from-device {:devices {} 
                                                :applications {}
                                                :instances {instance instance}} 
                                               #inst "2000"))
             => #(empty? (:instances %))))
