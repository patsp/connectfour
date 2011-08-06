(ns connectfour.core
  (:use [connectfour.model.model])
  (:use [connectfour.view.cliview])
  (:gen-class))

(defn -main [& args]
  (let [model (make-model 6 7)
        view (make-view)]
    (set-initial-player model player1)
    (render-model view model)
    (throw-game-piece model 0)
    (render-model view model)))

