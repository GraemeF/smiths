(ns smiths.core-test
  (:require [midje.sweet :refer [facts fact every-checker contains has]])
  (:require [smiths.core :refer :all]))

(facts "about `add-instance`"
       (let [estate (add-instance empty-estate #inst "2000")]
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
                      (count (:instances (add-instance estate #inst "2000")))
                      => 2))
         (facts "about `remove-instance`"
                (fact "it removes an instance from the estate"
                      (remove-instance estate #inst "2000"))
                => #(empty? (:instances %)))))

(facts "about starting a process"
       (fact "it adds a process to the instance"
             (start-process empty-estate #inst "2000")
             => #(= 1 (count (:processes %)))))
