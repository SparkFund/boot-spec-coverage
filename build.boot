(def version "0.5.0-SNAPSHOT")

(task-options!
 pom {:project 'sparkfund/boot-spec-coverage
      :version version
      :description "Boot plugin for clojure.spec coverage."}
 push {:repo-map {:url "https://clojars.org/repo/"}})

(set-env!
 :resource-paths #{"src"}
 :source-paths #{"test"}
 :dependencies
 '[[org.clojure/clojure "1.9.0-alpha17"]
   [adzerk/bootlaces "0.1.13" :scope "build"]
   [adzerk/boot-jar2bin "1.1.0" :scope "build"
    :exclusions [org.clojure/clojure]]
   [adzerk/boot-test "1.2.0"]
   [big-solutions/boot-mvn "0.1.5"
    :exclusions [org.clojure/clojure]]
   [org.clojure/tools.namespace "0.2.11"
    :exclusions [org.clojure/clojure]]])

(require '[adzerk.boot-jar2bin :refer :all]
         '[adzerk.boot-test :as bt]
         '[boot-mvn.core :refer [mvn]]
         '[clojure.java.io :as io])

(deftask deps
  [])

;; build

(replace-task!
 [i install] (fn [& xs]
               (comp (pom) (jar) (apply i xs))))

(deftask test
  "Run every non-integration test."
  []
  (bt/test
    :exclusions #{;; depends on boot.test
                  'sparkfund.boot-spec-coverage
                  ;; utility file for actual unit tests
                  'sparkfund.boot-spec-coverage.coverage-test}))

(require '[adzerk.bootlaces :refer :all])

(bootlaces! version)

(deftask release
  []
  (comp (build-jar) (push-release)))

