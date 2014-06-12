(ns syncserver.core
  (:require [clojure.data :as d])
  (:import syncserver.ISyncFunction))

(def state (atom {}))

(defn dissoc-in [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn modify [path f]
  (fn [s] (update-in s path f)))

(defn mk-fn [^ISyncFunction f]
  (fn [x] (.invoke f x)))

(defn jmodify [f path-array]
  (modify (map keyword (into [] path-array)) (mk-fn f)))

(defn jconj [e path-array]
  (let [path (map keyword (into [] path-array))]
    (modify path (fn [v] (conj v e)))))

(defn delete [path]
  (fn [s] (dissoc-in s path)))

(defn jdelete [path-array]
  (delete (map keyword (into [] path-array))))


(defn change [path v]
  (modify path (constantly v)))

(defn jchange [v path-array]
  (change (map keyword  (into [] path-array)) v))


(defn send-delta [os ns]
  (println (d/diff os ns))
  (println "old state:" os)
  (println "new state:" ns))

(defn commit [tx]
  (let [old-state @state
        tx-function (apply comp (reverse tx))
        new-state (tx-function old-state)]
    (println tx)    
    (reset! state new-state)
    (send-delta old-state new-state)))



