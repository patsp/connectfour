(ns connectfour.view.cliview
    (:use [connectfour.model.model]))

(defn make-view
  "Creates a new view. In the case of a CLI view there is
  nothing to create. Thus, just return nil for now."
  []
  nil)

(defn render-model
  "Displays the model."
  [view model]
  (let [n-rows (n-rows model)
        n-cols (n-cols model)]
    (doseq [r (range (dec n-rows) -1 -1)]
      (println
       (apply str
              (for [c (range n-cols)]
                (let [player (player-at-row-col model r c)]
                  (cond
                   (= player player1) \O
                   (= player player2) \X
                   true \.))))))))

