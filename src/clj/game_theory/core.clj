(ns game-theory.core
  (:require [clojure.contrib.combinatorics :as combo]))


(defn utility-table
  "Given the elements in a game, appropriately structured, returns a
  map with the actions of each player as the key, and the associated
  payoffs to the corresponding player as the value."
  [& elements]
  (let [create-map (fn [m [actions utils]]
                     (assoc m (vec actions) utils))]
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
    (fn [player actions]
      (nth (get u-table (vec actions)) player))))

(defn game
  "returns a game from the number of players, the strategies for each
   player, and the elements that define the utility function."
  [N A & game-elements]
  {:players (range N)
   :actions A
   :utility-fn (apply build-utility-fn game-elements)})

(defn players 
  "returns the number of players in the game."
  [game]
  (:players game))

(defn actions 
  "returns the player strategies in the game. If a player parameter is
   provided (index of the player, zero-started) then only the
   strategies for the specified player will be returned."
  ([game] (:actions game))
  ([game player] (nth (actions game) player)))

(defn utility
  "returns the utility value of a game for a player and a given startegy
  vector. The strategies are expected to be a sequence of all the
  strategies that all players are following."
  [game player actions]
  ((:utility-fn game) player actions))

(defn action-space
  "returns a sequence of all the action combinations or action
	profiles possible in a given game."
  [game]
  (apply combo/cartesian-product (actions game)))

(defn iff 
  "returns the 'a if and only if b' boolean value."
  [a b]
  (or (and a b)
      (and (not a) (not b))))

(defn one?
  "returns true if the supplied column is of length one"
  [coll]
  (= (count coll) 1))

(defn only-change?
  "accepts a player index, and new and old action profiles; returns
  true if and only if the supplied player's action is the only one to
  change."
  [player a-old a-new]
  (let [false-vec (filter false? (map = a-old a-new))]
    (if (one? false-vec)
      (not (nth false-vec player))
      false)))

(defn action-dominance
  [comparator game action player]
  (let [player-util (fn [x] (utility game player x))]))

(def prisoners-dilemma
  (game 2 [[:confess :deny] [:confess :deny]]
        [[:confess :confess] [1, 1]]
        [[:confess :deny]    [4, 0]]
        [[:deny :confess]    [0, 4]]
        [[:deny :deny]       [3, 3]]))
