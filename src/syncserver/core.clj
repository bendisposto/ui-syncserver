(ns syncserver.core
  (:gen-class)
  (:require [clojure.data :as d])
  (:import syncserver.IFunction1 
           syncserver.IFunction2 
           groovy.lang.Closure))

(def current-state (atom {}))

(defn arg-count [f]
  (let [m (first (.getDeclaredMethods (class f)))
        p (.getParameterTypes m)]
    (alength p)))

(defn delta [old-state]
  (println (d/diff old-state @current-state))
  (println "old state:" old-state)
  (println "new state:" @current-state))

(defn groovy [s x cls]
      (case (.getMaximumNumberOfParameters cls)
         0 (.call cls)
         1 (.call cls x)
         2 (.call cls [x s])))

(defn transform [[p e]]
 (let [t (type e)]
 (fn [s] 
   (cond
      (contains? (supers t) groovy.lang.Closure) 
         (update-in s p #(groovy s % e))
      (and (fn? e) (= 1 (arg-count e))) (update-in s p e)
      (and (fn? e) (= 2 (arg-count e))) (update-in s p (partial e s))
      :else (update-in s p (constantly e))))))

(defn transact [state txs] 
  (let [transfunc (->> txs (map transform) reverse (apply comp))
        new-state (transfunc state)]
         (reset! current-state new-state)
         new-state
        ))

(comment
  (defn -main [& args]
  (let [tt (Eval/me "[[['a'], {->1}], [['c'], true], [['b','c'],{->2}], [['a'],{x->x-2}], [['b','c'],{x,s -> s['a']*x*x}]]")]
    (println (transact {} tt)))

  (let [tt [[["a"] 1] [["a"] inc] [["b"] 5] [["a"] (fn [s e] (* e (s "b")))]]]
    (println (transact {} tt)))))



