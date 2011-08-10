;  Copyright (c) Patrick Spettel, 2011. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns connectfour.core
  (:use [connectfour.model.model])
  (:use [connectfour.view.guiview])
  (:gen-class))

(defn -main [& args]
  (let [model (make-model 6 7)
        view (make-view model)]
    (set-initial-player model player1)
    (loop [finished false]
      (if finished
        (render-model view model)
        (do
          (render-model view model)
          (when (throw-game-piece model (next-turn view model))
            (change-player model))
          (recur (or (board-full? model) (not (nil? (winner model))))))))))

