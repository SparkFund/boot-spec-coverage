;; don't run the tests in here, this file is used
;; to test the boot tool via sparkfund.boot-spec-coverage.core-test
(ns sparkfund.boot-spec-coverage.coverage-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]))

(s/fdef foo1
  :args (s/cat :i integer?)
  :ret integer?)
(defn foo1 [a]
  a)

(deftest foo1-test
  (is (foo1 1)))

(s/fdef foo2
  :args (s/cat :i integer?)
  :ret integer?)
(defn foo2 [a]
  a)

#_ ;; missing
(deftest foo2-test
  (is (foo2 1)))

(s/fdef foo3
  :args (s/cat :i integer?)
  :ret integer?)
(defn ^:spark/no-boot-spec-coverage foo3 [a]
  a)
