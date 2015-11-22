(ns ^:figwheel-always othello.core
  (:require [othello.game]
            [othello.tictactoe :as tictactoe]
            [othello.othello :as othello]
            [othello.view :as view]
            [clojure.string :as string]
            [reagent.core :as reagent :refer [atom]]
            [cljs.test :refer-macros [is testing]]))

(enable-console-print!)

(defonce app-state
  (atom nil))

(defn main-view []
  [:center
   [:h1 (string/capitalize
         (name
          (:type @app-state "Choose a game")))]
   [:h2
    (case (:status @app-state)
      :player-victory "You won! "
      :computer-victory "Computer wins. "
      :draw "Draw. "
      "")]
   [:div
    [:button
     {:on-click
      (fn new-game-click [e]
        (reset! app-state (tictactoe/new-game 3)))}
     "New Tic Tac Toe Game"]
    [:button
     {:on-click
      (fn new-game-click [e]
        (reset! app-state (othello/new-game 8)))}
     "New Othello Game"]]
   (when @app-state
     [view/game-board app-state])])

(defn ^:export main []
  (when-let [app (. js/document (getElementById "app"))]
    (reagent/render-component [main-view] app)))

(main)

(defn on-js-reload []
  (main))
