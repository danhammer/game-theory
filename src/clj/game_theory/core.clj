(ns game-theory.core
  (:require [clojure.contrib.combinatorics :as combo]))


(defn utility-table
  "Given the elements in a game, appropriately structured, returns a
  map with the actions of each player as the key, and the associate
  payoffs to the corresponding player as the value."
  [& elements]
  (let [create-map (fn [m [strategies utilities]]
                     (assoc m (vec strategies) utilities))]
    (reduce create-map {} elements)))

(defn utility-fn 
  "Builds an utility function from the element arguments. Each element
  is in the form

		[[a1, a2, ... an] [u1, u2, ... un]]

  where the vector [a1, a2, ... an] is the action profile for i = 1,
  2, ..., n and [u1, u2, ... un] lists the outcome utilities for all
  the players in that situation."
  [& elements]
  (let [u-table (apply utility-table elements)]
    (fn [player strategies]
      (nth (get u-table (vec strategies)) player))))

(defn game
   "Returns a game from the number of players, the strategies for each
   player, and the elements that define the utility function."
   [p s & elem]
   {:players (range p)
   :strategies s
   :utility-fn (apply build-utility-fn elem)})

(defn players 
	"returns the number of players in the game."
	[game]
   (:players game))

(defn strategies 
   "returns the player strategies in the game. If a player parameter is
   provided (index of the player, zero-started) then only the
   strategies for the specified player will be returned."
   ([game] (:strategies game))
   ([game player] (nth (strategies game) player)))

(defn utility
  "returns the utility value of a game for a player and a given startegy
  vector. The strategies are expected to be a sequence of all the
  strategies that all players are following."
  [game player strategies]
  ((:utility-fn game) player strategies))

(defn strategy-space 
	"returns a sequence of all the strategy combinations possible in a
	given game."
   [game]
   (apply combo/cartesian-product (strategies game)))

(defn iff 
  "Computes the 'a if and only if b' boolean value."
  [a b]
  (or (and a b)
      (and (not a) (not b))))

(defn single?
  "returns true if the supplied column is of length one"
  [coll]
  (= (count coll) 1))

(defn only-change?
  "accepts a player index and a new and old action profile; returns
  true if and only if the supplied player's action is the only one to
  change."
  [player a-old a-new]
  (let [bool-vec  (map = a-old a-new)
        false-vec (filter false? bool-vec)]
    (if (single? false-vec)
      (not (nth false-vec player))
      false)))

(def prisoners-dilemma
  (game 2 [[:confess :deny] [:confess :deny]]
        [[:confess :confess] [1, 1]]
        [[:confess :deny]    [4, 0]]
        [[:deny :confess]    [0, 4]]
        [[:deny :deny]       [3, 3]]))
