(ns game-theory.core-test
  (:use [midje sweet]
        game-theory.core))

(def prisoners-dilemma
  (game 2 [[:confess :deny] [:confess :deny]]
        [[:confess :confess] [1, 1]]
        [[:confess :deny]    [4, 0]]
        [[:deny :confess]    [0, 4]]
        [[:deny :deny]       [3, 3]]))

(fact
  (nash-equilibrium? prisoners-dilemma [:deny :deny]) => false
  (nash-equilibria prisoners-dilemma) => [[:confess :confess]])
