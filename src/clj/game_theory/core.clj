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
   :utility-fn (apply utility-fn game-elements)})

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
  change.  The function is useful in identifying an optimal action
  profile, conditional on the actions of all other players remaining
  the same."
  [player a-old a-new]
  (let [bool-vec (map = a-old a-new)
        false-vec (filter false? bool-vec)]
    (if (one? false-vec)
      (not (nth bool-vec player))
      false)))

(defn limited-profiles
  "accepts a game, player index, and the action of that player;
  returns all action profiles such that the player plays the specified
  action"
  [game player action])

(defn recursive-true?
  "accepts a nested collection where every element, no matter what
  level of nesting, is a boolean; returns true iff all elements are
  true."
  [nested-coll]
  (every? true? (flatten nested-coll)))

(defn action-dominance?
  "accepts a comparator (> or >=), a game, a player, and an action of
  that player. returns true if that player's action dominates all
  other possible actions in that game.  Note that the first list
  comprehension limits the space to the action profiles with the
  specified player's specified action; the second list comprehension
  takes the action profiles from the first list comprehension and
  finds the alternative actions of the specified players.  Then,
  finally, the outcome utilities associated with the player's action
  and the player's alternative action are compared.  If the specified
  player's action always yields a higher utility, then it is
  dominant."
  [comparator game player action]
  (let [aspace (action-space game)
        util (fn [x] (utility game player x))
        spec-action? (fn [a] (= (nth a player) action))]
    (recursive-true?
     (for [ai (filter spec-action? aspace)]
       (for [alt-ai (filter (partial only-change? player ai) aspace)]
         (comparator (util ai) (util alt-ai)))))))

(defn best-response?
  "accepts a game, player, and action profile across all players.
  returns true if the player's action in the supplied action profile
  is weakly preferred to all other possible player actions,
  conditional on the other players' actions remaining the
  same (i.e. a_{-i})"
  [game player action-profile]
  (let [util (fn [x] (utility game player x))]
    (recursive-true?
     (for [a (action-space game) :when (only-change? player action-profile a)]
       (>= (util action-profile) (util a))))))

(defn nash-equilibrium? 
  "returns true of the supplied action profile represents a nash
  equilibrium for the supplied game."
  [game action-profile]
  (let [p-set (players game)]
    (every? true?
            (map #(best-response? game % action-profile) p-set))))

(defn nash-equilibria 
	"returns all nash equilibria for the supplied game"
	[game]
   (filter (partial nash-equilibrium? game)
           (action-space game)))
