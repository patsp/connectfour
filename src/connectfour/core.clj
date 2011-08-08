(ns connectfour.core
  (:use [connectfour.model.model])
  (:use [connectfour.view.cliview])
  (:gen-class))

(defn -main [& args]
  (let [model (make-model 4 4)
        view (make-view)]
    (set-initial-player model player1)
    (loop [finished false]
      (if finished
        (render-model view model)
        (do
          (render-model view model)
          (throw-game-piece model (next-turn view model))
          (change-player model)
          (recur (not (nil? (winner model)))))))))

