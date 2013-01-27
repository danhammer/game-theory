game-theory
===========

Game theory in Clojure.  This project follows the text [A Course in
Game
Theory](http://www.amazon.com/Course-Game-Theory-Martin-Osborne/dp/0262650401)
used for the game theory sequence through the Berkeley economics
department.

## core namespace

The core namespace sets up the components of pure strategy game
theory, including the specification of a game itself.  A game is
determined by the players, the actions available to each player (from
which the action profiles are determined), and the utility (or payoff)
functions for each player.  Note that Clojure is ideal for this, since
you can supply the utility functions as arguments to other functions.

Take, for example, the prisoner's dilemma game, defined as follows:

```clojure
(def prisoners-dilemma
  (game 2
        [[:confess :deny] [:confess :deny]]

        [[:confess :confess] [1, 1]]
        [[:confess :deny]    [4, 0]]
        [[:deny :confess]    [0, 4]]
        [[:deny :deny]       [3, 3]]))
```

This is a two-player game, where the player actions are identical: a
player can either confess to the crime or deny involvement.  The
police offer the suspects a deal to try to coerce mutual confessions:
"If you deny, but your partner spills the beans, then you will
suffer."  The best combined option is that both players deny
involvement, but each player has the incentive to be a free rider.
One player's utility from denying, conditional on the other player's
confession, is higher than confessing.  Thus, the only Nash
equilibrium is that they both confess, which is the outcome desired by
the police (who designed the game's payouts).

The `core.clj` namespace contains many supporting functions that
ultimately yield the `nash-equilibria` function:

```clojure
(defn nash-equilibria 
	"returns all nash equilibria for the supplied game"
	[game]
   (filter (partial nash-equilibrium? game)
           (action-space game)))
```

This function can be seen in action within the [test
namespace](https://github.com/danhammer/game-theory/blob/master/test/game_theory/core_test.clj):

```clojure
(nash-equilibria prisoners-dilemma) => [[:confess :confess]]
```
