(ns smiths.core-test
  (:require [midje.sweet :refer [facts fact every-checker contains has]])
  (:require [smiths.core :refer :all]))

(facts "about `add-application-to-device`"
       (let [estate (add-application-to-device empty-estate #inst "2000")]
         (fact "it adds an application to the estate"
               (count (:applications estate)) => 1)
         (fact "it adds a device to the estate"
               (count (:devices estate)) => 1)
         (fact "it adds an instance to the estate"
               (count (:instances estate)) => 1)
         (fact "the added application looks like an application"
               (keys (first (:applications estate)))
               => (contains #{:manufacturer :name :version}))
         (facts "adding another instance"
                (fact "the estate contains 2 instances"
                      (count (:instances (add-application-to-device estate #inst "2000")))
                      => 2))
         (facts "about `remove-application-from-device`"
                (fact "it removes an instance from the estate"
                      (remove-application-from-device estate 
                                                      #inst "2000"))
                => #(empty? (:instances %)))))

(facts "about starting a process"
       (fact "it adds a process to the instance"
             (start-process empty-estate #inst "2000")
             => #(= 1 (count (:processes %)))))
