(task-options!
 pom {:project 'sparkfund/spec-coverage
      :version "0.3.0-SNAPSHOT"
      :description "Boot plugin for clojure.spec coverage."}
 push {:repo-map {:url "https://clojars.org/repo/"}})

(set-env!
 :resource-paths #{"src"}
 :source-paths #{"test"}
 :dependencies
 '[[adzerk/boot-jar2bin "1.1.0" :scope "build"
    :exclusions [org.clojure/clojure]]
   [adzerk/boot-test "1.2.0"]
   [big-solutions/boot-mvn "0.1.5"
    :exclusions [org.clojure/clojure]]
   [org.clojure/tools.namespace "0.2.11"
    :exclusions [org.clojure/clojure]]])

(require '[adzerk.boot-jar2bin :refer :all]
         '[adzerk.boot-test :refer :all]
         '[boot-mvn.core :refer [mvn]]
         '[clojure.java.io :as io])

(deftask deps
  [])

;; build

(replace-task!
 [i install] (fn [& xs]
               (comp (pom) (jar) (apply i xs))))

(deftask deploy
  []
  (comp (pom) (jar) (push)))
