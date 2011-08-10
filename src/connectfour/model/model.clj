;  Copyright (c) Patrick Spettel, 2011. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns connectfour.model.model)

(defrecord Board [curr-player ;; the player at turn
                  board-repr ;; internal board representation
                  n-rows ;; number of rows in the board
                  n-cols ;; number of columns in the board
                  ;; stores the position of where to insert the next
                  ;; piece for every column
                  next-insert-pos-for-cols
                  ;; [row col] of the position of the most recent
                  ;; insertion
                  prev-insert-pos])


;; player defintions

(def no-player :no-player)
(def player1 :player1)
(def player2 :player2)


;; access functions

(defn curr-player-ref
  [model]
  (:curr-player model))

(defn curr-player
  [model]
  @(curr-player-ref model))

(defn board
  [model]
  (:board-repr model))

(defn n-rows-ref
  [model]
  (:n-rows model))

(defn n-rows
  [model]
  @(n-rows-ref model))

(defn n-cols-ref
  [model]
  (:n-cols model))

(defn n-cols
  [model]
  @(n-cols-ref model))

(defn next-insert-pos-ref
  [model col]
  ((:next-insert-pos-for-cols model) col))

(defn next-insert-pos
  [model col]
  @(next-insert-pos model col))

(defn player-at-row-col-ref
  [model row col]
  (when (and (< row (n-rows model))
             (< col (n-cols model)))
    (((board model) row) col)))

(defn player-at-row-col
  [model row col]
  @(player-at-row-col-ref model row col))

(defn prev-insert-pos-ref
  [model]
  (:prev-insert-pos model))

(defn prev-insert-pos
  [model]
  @(prev-insert-pos-ref model))


;; model functions

(defn make-model
  "Returns a new connectfour model with the given number of
  rows and columns and returns the model."
  [n-rows n-cols]
  (Board.
   (ref no-player)
   ;; board-repr currently a vector of vectors containing refs of players
   (vec (for [r (range n-rows)] (vec (map ref (repeat n-cols no-player)))))
   (ref n-rows)
   (ref n-cols)
   ;; next-insert-pos-for-cols: initially all 0
   (vec (map ref (repeat n-cols 0)))
   ;; prev-insert-pos is initially
   (ref [nil nil])))

(defn set-initial-player
  "Sets the given model's current player to the given player
  and returns the (changed) model."
  [model player]
  (dosync
   (alter (curr-player-ref model) (fn [_] identity player)))
  model)

(defn change-player
  "Changes the current player:
  if the current player is player1, the current player gets player2
  and vice versa. Returns the (changed) model."
  [model]
  (dosync
   (alter (curr-player-ref model)
          (fn [_]
            (if
                (= (curr-player model) player1) player2
                player1))))
  model)

(defn throw-game-piece
  "Throws a game piece into the board into the given column.
  Returns the (changed) model."
  [model col]
  (let [n-rows (n-rows model)
        n-cols (n-cols model)]
    (when (< col n-cols)
      ;; get the row (insert-pos-ref) in which to change
      ;;the piece to the curr-player in the given col
      (let [insert-pos-ref (next-insert-pos-ref model col)]
        (when (< @insert-pos-ref n-rows)
          (dosync
           ;; first change the piece
           (alter
            (player-at-row-col-ref model @insert-pos-ref col)
            (fn [_] (curr-player model)))
           ;; then change update the prev-insert-pos
           (alter (prev-insert-pos-ref model) (fn [_] [@insert-pos-ref col]))
           ;; then change the row where the next piece in this column
           ;; is inserted
           (alter insert-pos-ref inc)
           true))))))

(defn gen-all-win-possibs-from-pos
  "Generate all possible 4 values from the given pos.
  I.e.: Just generate every possible diagonal, horizontal, vertical
  of size 4"
  [model [row col]]
  ;; it is enough to test in the area row +/- 4 col +/- 4 from
  ;; the previous insert position
  (mapcat identity
          (for [r (range (- row 4) (+ row 5))
                c (range (- col 4) (+ col 5))]
            ;; then just generate every possible
            ;; diagonal, horizontal, vertical
            ;; of size 4
            (filter #(and (not (= % nil)) (= (count %) 4))
                    (for [delta-r [-1 0 1] delta-c [-1 0 1]]
                      (when (not (and (= delta-r 0) (= delta-c 0)))
                        (filter #(not (= % nil))
                                (for [k [0 1 2 3]]
                                  (let [new-r (+ r (* k delta-r))
                                        new-c (+ c (* k delta-c))]
                                    (when (and (>= new-r 0)
                                               (< new-r (n-rows model))
                                               (>= new-c 0)
                                               (< new-c (n-cols model)))
                                      (player-at-row-col model
                                                         new-r new-c)))))))))))

(defn winner
  "Checks whether the game is finished and thus a player has won.
  Returns the winner or nil if the game has not yet finished."
  [model]
  (let [[row col] (prev-insert-pos model)]
    (when (and (not (= nil row)) (not (= nil col)))
      (reduce #(if (or (= player1 %2) (= player2 %2)) %2 %1)
              nil
              (map #(cond
                     ;; all 4 player1
                     (apply = player1 %) player1
                     ;; all 4 player2
                     (apply = player2 %) player2
                     true nil)
                   (gen-all-win-possibs-from-pos model [row col]))))))

(defn print-board
  [model]
  (println (board model)))

