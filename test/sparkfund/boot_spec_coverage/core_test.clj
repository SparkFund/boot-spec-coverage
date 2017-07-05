(ns sparkfund.boot-spec-coverage.core-test
  (:require [clojure.test :refer :all]
            [sparkfund.boot-spec-coverage.core :as cover]))

(deftest unexercised-var-test
  (is
		(do
			(require 'sparkfund.boot-spec-coverage.coverage-test)
			(try (cover/check-coverage 
						 ['sparkfund.boot-spec-coverage.coverage-test]
						 'sparkfund.boot-spec-coverage.coverage-test)
					 false
					 (catch clojure.lang.ExceptionInfo e
						 (is (= '#{sparkfund.boot-spec-coverage.coverage-test/foo2}
										(::cover/uncalled (ex-data e))))
             true)))))
