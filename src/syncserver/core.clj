(ns syncserver.core
  (:gen-class)
  (:require [clojure.data :as d]
            [clojure.set :as s])

  (:import syncserver.IFunction1
           syncserver.IFunction2
           groovy.lang.Closure))

(def current-state (atom {}))

(defn arg-count [f]
  (let [m (first (.getDeclaredMethods (class f)))
        p (.getParameterTypes m)]
    (alength p)))

(defn delta [old-state]
  (println "old state:" old-state)
  (println "new state:" @current-state)
  (d/diff old-state @current-state))

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

(declare ddiff)

(def a {:a {:b 1 :c 3} :c [17 19] :d {:e [1 2 3] :f 5}})
(def b (transact a [[[:a :c] -1] [[:d :e] (fn [v] (assoc v 1 10))]]))

(defn vector-diff-same-length [path a b diffs]
  #_(println "vector-diff-same-length" a b "@" path " : " diffs)
  (reduce (fn [d index]
            (let [itm (get b index)
                  oitem (get a index)]
              #_(println " processing" "a" oitem "b" itm "i" index)
              (ddiff (conj path index) oitem itm d))) diffs (range 0 (count b))))

(defn vector-diff [path a b diff]
  #_(println "vector-diff" a b "@" path " : " diff)
  (if (= a b)
    diff
    (let [[diffing rest] (split-at (count a) b)
          d1 (vector-diff-same-length path a (into [] diffing) diff)
          d2 (if (seq rest) (conj d1 [:concat path (into [] rest)]) d1)]
      (if (< (count b) (count a)) (conj d2 [:del path (into [] (range (count b) (count a)))])  d2))))

(defn map-diff-proc [path a b diffs]
  (reduce (fn [d ky]
            (ddiff (conj path ky) (get a ky) (get b ky) d)) diffs (keys b)))

(defn map-diff [path a b diffs]
  #_(println "map-diff" a b "@" path " : " diffs)
  (let [a-keys (into #{} (keys a))
        b-keys (into #{} (keys b))
        del-keys (s/difference a-keys b-keys)
        new-in-b (select-keys b (s/difference b-keys a-keys))
        pa (select-keys a (s/intersection a-keys b-keys))
        pb (select-keys b (s/intersection a-keys b-keys))
        d1 (map-diff-proc path pa pb diffs)
        d2 (if (seq del-keys) (conj d1 [:del-keys path (into [] del-keys)]) d1)
        d3 (if (seq new-in-b) (conj d2 [:merge path new-in-b]) d2)]
    d3))

(defn ddiff [path a b diffs]
  (println "ddiff" a b "@" path " : " diffs)
  (cond
   (= a b) diffs
   (every? vector? [a b]) (vector-diff path a b diffs)
   (every? map? [a b]) (map-diff path a b diffs)
   :otherwise (if (= a b) diffs (conj diffs [:set path b]))))

(comment
  (defn -main [& args]
    (let [tt (Eval/me "[[['a'], {->1}], [['c'], true], [['b','c'],{->2}], [['a'],{x->x-2}], [['b','c'],{x,s -> s['a']*x*x}]]")]
      (println (transact {} tt)))

    (let [tt [[["a"] 1] [["a"] inc] [["b"] 5] [["a"] (fn [s e] (* e (s "b")))]]]
      (println (transact {} tt)))))
