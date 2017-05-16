(ns spec-coverage.core
  (:require [clojure.spec.test :as stest]
            [clojure.test :as test]
            [clojure.set :as set]))

(def cover-ns-file "0123010221cover-ns.tmp")

(def spec-used (atom #{}))

(def no-coverage-kw ::no-coverage)

(defn wrap-usage-check [vsym]
  {:pre [(symbol? vsym)]}
  (let [v (find-var vsym)
        f (when (bound? v) @v)]
    (when f
      (alter-var-root
        v
        (constantly
          (fn [& args]
            (swap! spec-used conj vsym)
            (apply f args)))))))

(defn summarize-results [instrumented]
  {:pre [(set? instrumented)]}
  (let [uncalled (set/difference instrumented @spec-used)]
    (when (seq uncalled)
      (throw (ex-info
               (apply str "Please write tests for these specs: \n - "
                      (interpose "\n - " (sort (map str uncalled))))
               {})))))

(defn to-instrument [nss]
  (into #{}
        (comp 
          (mapcat (fn [ns]
                    (stest/enumerate-namespace (ns-name ns))))
          (filter (stest/instrumentable-syms))
          (remove
            (fn [vsym]
              (-> (find-var vsym)
                  meta
                  no-coverage-kw))))
        nss))

(defn instrument [checks instrument-fn]
  (let [_ (run! wrap-usage-check checks)
        instrumented (set (instrument-fn checks))
        _ (assert (= checks instrumented))]
    (prn "instrumented" instrumented)
    instrumented))

(defn check-coverage [ns test-ns]
  {:pre [(coll? ns)]}
  (let [instrumented (instrument (to-instrument ns))
        _ (require test-ns)]
    (test/run-tests test-ns)
    (summarize-results instrumented)))

(defn pre-startup [cover-ns instrument-fn]
  (prn "pre-startup")
  (spit cover-ns-file
        (binding [*print-dup* true]
          (pr-str {:cover-ns cover-ns
                   :instrument-fn instrument-fn}))))

(defn startup []
  (prn "startup")
  (let [{:keys [cover-ns instrument-fn]} (read-string (slurp cover-ns-file))
        _ (assert (or (nil? instrument-fn) ((every-pred symbol? namespace) instrument-fn)))
        _ (assert (every? symbol? cover-ns))
        instrument-fn (or (when instrument-fn
                            (resolve instrument-fn))
                          stest/instrument)
        _ (run! require cover-ns)
        _ (instrument (to-instrument cover-ns) instrument-fn)]
    (prn "end startup")
    nil))

(defn shutdown []
  (prn "shutdown")
  (summarize-results (to-instrument (:cover-ns (read-string (slurp cover-ns-file)))))
  (prn "end shutdown")
  nil)

(comment
  (require 'spec-coverage.coverage-test)
  (check-coverage ['spec-coverage.coverage-test]
                  'spec-coverage.coverage-test)
  )
