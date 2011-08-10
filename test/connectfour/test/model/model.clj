;  Copyright (c) Patrick Spettel, 2011. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns connectfour.test.model.model
  (:use [connectfour.model.model])
  (:use [clojure.test]))

(deftest test-make-model
  "Makes a model with make-model and tests whether
  the returned model is correct."
  (let [model (make-model 6 7)]
    (is (= (curr-player model) no-player) "wrong curr-player")
    (is (= (n-rows model) 6) "wrong number of rows")
    (is (= (n-cols model) 7) "wrong number of columns")
    ;; checks whether whole board is filled with no-player
    (is (reduce (fn [acc [r c]] (and
                                 acc
                                 (= no-player(player-at-row-col model r c))))
                true
                (for [r (range (n-rows model))
                      c (range (n-cols model))] [r c]))
                "wrong content")))

(deftest test-set-initial-player
  "Sets the initial player and tests whether this works."
  (let [model (make-model 6 7)]
    (is (= (curr-player model) no-player) "wrong curr-player")
    (is (= (curr-player (set-initial-player model player1))
           player1)
        "wrong curr-player")
    (is (= (curr-player
            (set-initial-player model player2))
           player2)
        "wrong curr-player")))

(deftest test-change-player
  "Changes the player and tests whether this works."
  (let [model (make-model 6 7)]
    (is (= (curr-player
            (set-initial-player model player1))
           player1)
        "wrong curr-player")
    (is (= (curr-player
            (change-player model))
           player2)
        "wrong curr-player")
    (is (= (curr-player
            (change-player model))
           player1)
        "wrong curr-player")))

(deftest test-throw-game-piece
  (let [model (make-model 6 7)]
    (is (= (player-at-row-col
            (do
              (set-initial-player model player1)
              (throw-game-piece model 0)
              model)
            0 0) player1))
    (is (= (player-at-row-col
            (do
              (throw-game-piece model 0)
              model)
            1 0) player1))
    (is (= (player-at-row-col model 2 0) no-player))
    (is (= (player-at-row-col model 2 5) no-player))
    (is (= (player-at-row-col
            (do (throw-game-piece model 0) model)
            2 0) player1))))

(deftest test-winner-vertical
  (let [model (make-model 4 4)]
    (is (= nil
           (winner
            (do
              (set-initial-player model player1)
              (throw-game-piece model 0)
              model))))
    (is (= nil
           (winner
            (do
              (throw-game-piece model 0)
              model))))
    (is (= nil
           (winner
            (do
              (throw-game-piece model 0)
              model))))
    (is (= player1
           (winner
            (do
              (throw-game-piece model 0)
              model))))))

(deftest test-winner-horizontal
  (let [model (make-model 4 4)]
    (is (= nil
           (winner
            (do
              (set-initial-player model player1)
              (throw-game-piece model 0)
              model))))
    (is (= nil
           (winner
            (do
              (throw-game-piece model 1)
              model))))
    (is (= nil
           (winner
            (do
              (throw-game-piece model 2)
              model))))
    (is (= player1
           (winner
            (do
              (throw-game-piece model 3)
              model))))))

(deftest test-winner-diagonal
  (let [model (make-model 4 4)]
    (is (= nil
           (winner
            (do
              (set-initial-player model player1)
              (throw-game-piece model 0)
              model))))
    (is (= nil
           (winner
            (do
              (change-player model) ;; change to player2
              (throw-game-piece model 1)
              (change-player model) ;; change back to player1
              (throw-game-piece model 1)
              model))))
    (is (= nil
           (winner
            (do
              (change-player model) ;; change to player2
              (throw-game-piece model 2)
              (throw-game-piece model 2)
              (change-player model) ;; change back to player1
              (throw-game-piece model 2)
              model))))
    (is (= player1
           (winner
            (do
              (change-player model) ;; change to player2
              (throw-game-piece model 3)
              (throw-game-piece model 3)
              (throw-game-piece model 3)
              (change-player model) ;; change back to player1
              (throw-game-piece model 3)
              model))))))
