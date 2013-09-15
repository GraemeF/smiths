(defproject smiths "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-time "0.6.0"]
                 [org.clojure/data.generators "0.1.0"]
                 [clojurewerkz/eep "1.0.0-alpha4"]]
  :main smiths.core
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[midje "1.5.1"]]}})
