(ns syncserver.core
  (:require [clojure.data :as d])
  (:import syncserver.IFunction1 syncserver.IFunction2))



(defn delta [os ns]
  (println (d/diff os ns))
  (println "old state:" os)
  (println "new state:" ns))

(defn groovy [s cls]
 (fn [x]
   (let [n (.getMaximumNumberOfParameters cls)]
     ; (println x s cls)
      (case n
         0 (.call cls)
         1 (.call cls x)
         2 (.call cls [x s])))))

(defn super-class-names [d]
  (into #{} (map #(.getName %) (supers (type d)))))

(defn transform [[p e]]
 (let [t (type e)]
 (fn [s] 
   (cond
      (contains? (super-class-names t) "groovy.lang.Closure") (update-in s p (groovy s e))
      (fn? e) (update-in s p e)
      :else (update-in s p (constantly e))))))

(defn transact [state txs] ((->> txs (map transform) reverse (apply comp)) state))


