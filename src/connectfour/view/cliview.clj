(ns connectfour.view.cliview
  (:use [connectfour.model.model]))

(def player-reprs {player1 \O player2 \X no-player \.})
(def player-names {
                   player1 "Player 1 (O)"
                   player2 "Player 2 (X)"
                   no-player "No Player (.)"
                   })

(defn make-view
  "Creates a new view. In the case of a CLI view there is
  nothing to create. Thus, just return nil for now."
  []
  nil)

(defn render-model
  "Displays the model."
  [view model]
  (let [n-rows (n-rows model)
        n-cols (n-cols model)
        win (winner model)]
    (if (nil? win)
      (do
        (print "It is ")
        (print (player-names (curr-player model)))
        (println "'s turn."))
      (do
        (print "Game finished. ")
        (print (player-names win))
        (println " is the winner.")))
    (doseq [r (range (dec n-rows) -1 -1)]
      (println
       (apply str
              (for [c (range n-cols)]
                (let [player (player-at-row-col model r c)]
                  (player-reprs player))))))))

(defn next-turn
  "Returns the column where the user wants to insert the new piece.
  Asks the user again as long as a wrong number is entered."
  [view model]
  (loop [col nil]
    (if (not (nil? col))
      col
      (do
        (print "Please enter the column to insert the piece ")
        (print "(0-")
        (print (dec (n-cols model)))
        (println "): ")
        (let [c (try
                  (Integer/parseInt (read-line))
                  (catch NumberFormatException _ nil))]
          (if (or (nil? c) (< c 0) (>= c (n-cols model)))
            (do
              (println "Invalid number entered. Try again!")
              (recur nil))
            (recur c)))))))
