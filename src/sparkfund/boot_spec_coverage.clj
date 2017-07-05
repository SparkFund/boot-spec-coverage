(ns sparkfund.boot-spec-coverage
  (:refer-clojure :exclude [test])
  (:require [sparkfund.boot-spec-coverage.core :as cover]
            [boot.core :as core]
            [clojure.java.io :as io]
            [clojure.tools.namespace.find :refer [find-namespaces-in-dir]]
            [adzerk.boot-test :as bt]))

(defn ^:private all-ns* [& dirs]
  (distinct (mapcat #(find-namespaces-in-dir (io/file %)) dirs)))

(core/deftask spec-coverage
  "Run clojure.test tests in a pod. Throws on test errors or failures.

  The --namespaces option specifies the namespaces to test. The default is to
  run tests in all namespaces found in the project.

  The --exclusions option specifies the namespaces to exclude from testing.

  The --filters option specifies Clojure expressions that are evaluated with %
  bound to a Var in a namespace under test. All must evaluate to true for a Var
  to be considered for testing by clojure.test/test-vars.

  The --junit-output-to option specifies the path to a directory relative to the
  target directory where a junit xml file for each test namespace will be
  generated by using the clojure.test.junit facility. When present it will make
  the target to be synced even when there are test errors or failures.

  The --cover-ns option specifies Clojure namespaces that will be checked for
  clojure.spec coverage during unit testing."
  [c clojure    VERSION   str    "the version of Clojure for testing."
   n namespaces NAMESPACE #{sym} "The set of namespace symbols to run tests in."
   e exclusions NAMESPACE #{sym} "The set of namespace symbols to be excluded from test."
   f filters    EXPR      #{edn} "The set of expressions to use to filter namespaces."
   X exclude    REGEX     regex  "the filter for excluded namespaces"
   I include    REGEX     regex  "the filter for included namespaces"
   r requires   REQUIRES  #{sym} "Extra namespaces to pre-load into the pool of test pods for speed."
   s shutdown   FN        #{sym} "functions to be called prior to pod shutdown"
   S startup    FN        #{sym} "functions to be called at pod startup"
   j junit-output-to JUNITOUT str "The directory where a junit formatted report will be generated for each ns"
   C cover-ns		COVERAGE #{sym} "The set of namespace symbols to run coverage checking on"
   i instrument	FN sym "The function to instrument vars (default: clojure.spec.test/instrument). Its namespace will be required before resolution."
   ]
  (let []
    (comp
      (core/with-pre-wrap fileset
        (cover/pre-startup
          (or cover-ns
              (apply all-ns* (->> fileset
                                  core/input-dirs
                                  (map (memfn getPath)))))
          instrument)
        fileset)
      (bt/test
        :requires (into #{'sparkfund.boot-spec-coverage.core} 
                        (concat requires 
                                (when instrument
                                  (assert ((every-pred symbol? namespace) instrument))
                                  [(symbol (namespace instrument))])))
        :startup (into `#{cover/startup} startup)
        :shutdown (into `#{cover/shutdown} shutdown)
        :clojure clojure
        :namespaces namespaces
        :exclusions exclusions
        :filters filters
        :exclude exclude
        :include include
        :junit-output-to junit-output-to))))
