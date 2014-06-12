(ns syncserver.core
  (:require [clojure.data :as d])
  (:import syncserver.ISyncFunction))

(def tx (ref '()))
(def state (ref {}))

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
  (dosync (alter tx conj (fn [s] (update-in s path f)))))

(defn mk-fn [^ISyncFunction f]
  (fn [x] (.invoke f x)))

(defn jmodify [f path-array]
  (modify (map keyword (into [] path-array)) (mk-fn f)))


(defn delete [path]
  (dosync (alter tx conj (fn [s] (dissoc-in s path)))))

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

(defn commit []
  (let [old-state @state
        tx-function (apply comp @tx)
        new-state (tx-function old-state)]
    (dosync (ref-set state new-state)
            (ref-set tx '()))
    (send-delta old-state new-state)))



