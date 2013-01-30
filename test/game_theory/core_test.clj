(ns game-theory.core-test
  (:use [midje sweet]
        game-theory.core))

(def BoS
  (game 2
        [[:bach :strav]
         [:bach :strav]]

        [[:bach :bach]     [2, 1]]
        [[:bach :strav]    [0, 0]]
        [[:strav :bach]    [0, 0]]
        [[:strav :strav]   [1, 2]]))

(def coordination-game
  (game 2
        [[:bach :strav]
         [:bach :strav]]

        [[:bach :bach]     [2, 2]]
        [[:bach :strav]    [0, 0]]
        [[:strav :bach]    [0, 0]]
        [[:strav :strav]   [1, 1]]))

(fact
  "There are two Nash equilbria in a game where the players both want
  to act together, but do not agree on the best action."
  (nash-equilibria BoS) => [[:bach :bach] [:strav :strav]]
  
  "Still, the notion of Nash equilibria does not rule out two
  equilibria, even if the players agree on the best option.  The
  [:strav :strav] outcome is an inferior equilibrium; but it is still
  an equilibrium, since :strav is the best response of one player,
  when the other player plays :strav"
  (best-response? coordination-game 0 [:strav :strav]) => true
  (best-response? coordination-game 1 [:strav :strav]) => true
  (nash-equilibria coordination-game) => [[:bach :bach] [:strav :strav]])



(def prisoners-dilemma
  (game 2
        [[:confess :deny]
         [:confess :deny]]

        [[:confess :confess] [1, 1]]
        [[:confess :deny]    [4, 0]]
        [[:deny :confess]    [0, 4]]
        [[:deny :deny]       [3, 3]]))

(fact
  "There are combined gains from cooperation in the prisoner's
  dilemma; but barring coordination of action, then the mutual denial
  of activity is not a Nash equilibrium.  The police designed the
  game, and the only Nash equilibrium is their desired outcome."
  (nash-equilibrium? prisoners-dilemma [:deny :deny]) => false
  (nash-equilibria prisoners-dilemma) => [[:confess :confess]])

(def dove-hawk
  (game 2
        [[:dove :hawk]
         [:dove :hawk]]

        [[:dove :dove] [3, 3]]
        [[:dove :hawk] [1, 4]]
        [[:hawk :dove] [4, 1]]
        [[:hawk :hawk] [0, 0]]))

(fact
  "This does not bode well for nuclear warfare.  If one player acts
  like a Hawk, then the other player has the incentive to limit
  aggregate damage by acting like a Dove. If one player acts like a
  Dove, then the other player has the incentive to nuke them for world
  dominance, despite the ambient damage."
  (nash-equilibria dove-hawk) => [[:dove :hawk] [:hawk :dove]])

(def matching-pennies
  (game 2
        [[:head :tail]
         [:head :tail]]

        [[:head :head] [1, -1]]
        [[:head :tail] [-1, 1]]
        [[:tail :head] [-1, 1]]
        [[:tail :tail] [1, -1]]))

(fact
  "In this game, the interests of the players are diametrically
  opposed, such that the game is strictly competitive.  There are no
  Nash equilibria in a strictly competitive game."
  (nash-equilibria matching-pennies) => '())


(fact
  "checks to make sure that certain games are symmetric."
  (symmetric? prisoners-dilemma) => true
  (symmetric? dove-hawk) => true
  (symmetric? matching-pennies) => false
  (symmetric? BoS) => false
  (symmetric? coordination-game) => true)


(def development-game
  (game 2
        [[:lend :dont-lend]
         [:repay :dont-repay]]

        [[:lend :repay]           [3, 7]]
        [[:lend :dont-repay]      [-10, 20]]
        [[:dont-lend :repay]      [0, 0]]
        [[:dont-lend :dont-repay] [0, 0]]))

(fact
  "Just the fact that the borrower could walk away prevents the
opportunity to lend"
  (nash-equilibria development-game) => [[:dont-lend :dont-repay]])

(def development-game-collateral
  (game 2
        [[:lend :dont-lend]
         [:repay :dont-repay]]

        [[:lend :repay]           [3, 7]]
        [[:lend :dont-repay]      [2, 6]]
        [[:dont-lend :repay]      [0, 0]]
        [[:dont-lend :dont-repay] [0, 0]]))

(fact
  (nash-equilibria development-game-collateral) => [[:lend :repay]])
