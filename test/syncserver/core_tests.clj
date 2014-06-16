(ns syncserver.core-tests
  (:use midje.sweet)
  (:require [syncserver.core :refer :all]
            [clojure.test :refer :all]
            ))


;.;. One of the symptoms of an approaching nervous breakdown is the belief
;.;. that one's work is terribly important. -- Russell
(fact "vector diffs"
  (vector-diff [] [] :foo) => :foo
  (vector-diff [:a] [] #{}) => #{[:del [0]]}
  (vector-diff [:a] [:b] #{}) => #{[:set 0 :b]}
  (vector-diff [:a] [:b :c] #{}) => #{[:set 0 :b] [:concat [:c]]}
  (vector-diff [] [:a :b] #{}) => #{[:concat [:a :b]]}
) 

