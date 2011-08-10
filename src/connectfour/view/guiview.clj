;  Copyright (c) Patrick Spettel, 2011. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns connectfour.view.guiview
  (:use [connectfour.model.model])
  (:use [seesaw core border behave graphics])
  (:import [javax.swing SwingUtilities])
  (:import [java.awt Color]))

(def player-names {
                   player1 "Player 1 (red)"
                   player2 "Player 2 (yellow)"
                   no-player "No Player (white)"
                   })
(def player-colors {
                    player1 Color/RED
                    player2 Color/YELLOW
                    no-player Color/WHITE
                    })
(def lock (Object.))
(def click-col-pos (atom nil))

(defn make-panel [model]
  "Create a panel with a paint-function which renders
  the given model."
  (canvas
   :listen [:mouse-clicked (fn [e]
                             (locking lock
                               (do (swap! click-col-pos
                                          (fn [_] [(.getX e)
                                                   (.getY e)]))
                                   (.notifyAll lock))))]
   :paint (fn
            [c g]
            (let [width (.getWidth c)
                  height (- (.getHeight c) 50)
                  tile-width (/ width (n-cols model))
                  tile-height (/ height (n-rows model))
                  win (winner model)]
              (cond (not (nil? win))
                    (.drawString g (str "Game finished. "
                                        (player-names win)
                                        " is the winner.")
                                 0 20)
                    (board-full? model)
                    (.drawString g (str "Game finished. "
                                        "There is no winner.")
                                 0 20)
                    true
                    (.drawString g (str "It is "
                                        (player-names (curr-player model))
                                        "'s turn.")
                                 0 20))
              (loop [row (dec (n-rows model)) col 0]
                (when (and (< col (n-cols model)) (>= row 0))
                  (.setColor g (player-colors
                                (player-at-row-col model row col)))
                  (.fillOval g
                             (* col tile-width)
                             (+ 50 (* (- (dec (n-rows model)) row)
                                tile-height))
                             tile-width
                             tile-height)
                  (if (>= col (dec (n-cols model)))
                    (recur (dec row) 0)
                    (recur row (inc col)))))))))

(defn make-view
  "Creates a new view."
  [model]
  (let [panel (make-panel model)]
    (invoke-now
     (show!
      (frame
       :title "Connect Four"
       :content (border-panel
                 :center panel)
       :on-close :exit
       :size [600 :by 600])))
    panel))

(defn render-model
  "Displays the model.
  Most of the painting work is done in the canvas' paint function.
  So just repaint the canvas."
  [view model]
  (.repaint view))

(defn next-turn
  "Returns the column where the user wants to insert the new piece."
  [view model]
  (let [width (.getWidth view)
        height (.getHeight view)
        tile-width (/ width (n-cols model))
        tile-height (/ height (n-rows model))]
    (locking lock
      (loop []
        (if (nil? @click-col-pos)
          (do
            (.wait lock)
            (recur))
          ;; determine column where clicked
          (let [col (quot (@click-col-pos 0) tile-width)]
            (swap! click-col-pos (fn [_] nil))
            col))))))
