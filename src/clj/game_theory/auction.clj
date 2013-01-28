(ns game-theory.auction
  "Consider the auction with 3 players, who value the object according
  to the schedule:

  v = [30 20 10]

  The bids are limited to whole-dollar amounts, such that B = 1, 2, 3,
  ... and no player will ever bid more than they value the object."
  (:use game-theory.core)
  (:require [clojure.contrib.combinatorics :as combo]
            [incanter.core :as i]))

(def v [30 20 10])

(defn bid-range->key
  [N]
  (map (comp keyword str)
       (range (inc N))))

(defn key->int [k]
  (read-string (name k)))

(defn util-schedule
  [value-vector strategy-profile]
  (let [[v0 v1 v2] value-vector
        [b0 b1 b2] (map key->int strategy-profile)
        mx (reduce max [b0 b1 b2])]
    (cond
     (= b0 mx) [(- v0 b0) 0 0]
     (= b1 mx) [0 (- v1 b1) 0]
     (= b2 mx) [0 0 (- v2 b2)])))

(defn strategy-pair
  [strategy-profile]
  [strategy-profile (util-schedule strategy-profile)])

(defn game-elements
  [value-vector]
  (let [s-pair (fn [x] [(vec x) (util-schedule value-vector x)])
        strategies (apply combo/cartesian-product
                          (map bid-range->key value-vector))]
    (vec (map s-pair strategies))))

(def first-price-auction
  (let [strategies (vec (map (comp vec bid-range->key) v))]
    (apply game 3
           strategies
           (game-elements v))))

;; (def x (nash-equilibria first-price-auction))
;; (first x) => (:19 :19 :0)
;; (count x) => 22
;; ((:19 :19 :0)
;;  (:19 :19 :1)
;;  (:19 :19 :2)
;;  (:19 :19 :3)
;;  (:19 :19 :4)
;;  (:19 :19 :5)
;;  (:19 :19 :6)
;;  (:19 :19 :7)
;;  (:19 :19 :8)
;;  (:19 :19 :9)
;;  (:19 :19 :10)
;;  (:20 :20 :0)
;;  (:20 :20 :1)
;;  (:20 :20 :2)
;;  (:20 :20 :3)
;;  (:20 :20 :4)
;;  (:20 :20 :5)
;;  (:20 :20 :6)
;;  (:20 :20 :7)
;;  (:20 :20 :8)
;;  (:20 :20 :9)
;;  (:20 :20 :10))
