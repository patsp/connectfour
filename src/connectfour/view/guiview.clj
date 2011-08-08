(ns connectfour.view.guiview
  (:use [connectfour.model.model])
  (:use [seesaw core border behave graphics])
  (:import [javax.swing SwingUtilities]))

(defn make-panel []
  (paintable javax.swing.JPanel
             :paint (fn [c g] (.drawLine g 0 0 (.getWidth c) (.getHeight c)))))

(defn make-view
  "Creates a new view."
  []
  (let [panel (make-panel)]
    (do
      (invoke-later
       (show!
        (frame
         :title "Connect Four"
         :content (border-panel
                   :center panel)
         :on-close :exit
         :size [600 :by 600])))
      panel)))

(defn render-model
  "Displays the model."
  [view model])

(defn next-turn
  "Returns the column where the user wants to insert the new piece."
  [view model]
  0)

