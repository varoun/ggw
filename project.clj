(defproject ggw "0.1.0-SNAPSHOT"
  :description "Graphite http gateway"
  :url "https://github.com/varoun/ggw"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-redis "0.0.12"]
                 [ring "1.1.8"]
                 [compojure "1.1.5"]
                 [aleph "0.3.0-beta7"]
                 [org.clojure/tools.logging "0.2.6"]
                 [log4j/log4j "1.2.17" 
                  :exclusions 
                  [javax.mail/mail javax.jms/jms com.sun.jdmk/jmxtools com.sun.jmx/jmxri]]])
