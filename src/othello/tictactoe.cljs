(ns othello.tictactoe
  (:require
   [othello.game :as game :refer [draw? can-move? available? win?]]
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [deftest]]))

(defn new-game [n]
  {:type :tictactoe
   :status :in-progress
   :background-color "lightgrey"
   :player "P"
   :computer "C"
   :win-length 3
   :board-size n
   :board
   (vec (repeat n (vec (repeat n " "))))})

(defmethod draw? :tictactoe
  [{:keys [computer player board]} game]
  (every? #{computer player} (apply concat board)))

(deftest draw-test
  (is (not (draw? {:type :tictactoe
                   :player "P"
                   :computer "C"
                   :board [["P" " "]]})))
  (is (draw? {:type :tictactoe
              :player "P"
              :computer "C"
              :board [["P" "C"]]})))

(defn straight [owner board [x y] [dx dy] n]
  (every? true?
          (for [i (range n)]
            (= (get-in board [(+ (* dx i) x)
                              (+ (* dy i) y)])
               owner))))

(defmethod can-move? :tictactoe
  [{:keys [status board] :as game} x y player]
  (and (available? game x y)
       (update game :board assoc-in [y x] player)))

(defmethod win? :tictactoe
  [{:keys [board board-size win-length]} player]
  (some true?
        (for [i (range board-size)
              j (range board-size)
              dir [[1 0] [0 1] [1 1] [1 -1]]]
          (straight player board [i j] dir win-length))))

(deftest win-test
  (is (win? {:type :tictactoe
             :board [["P"]]
             :board-size 1
             :win-length 1} "P"))
  (is (not (win? {:type :tictactoe
                  :board [["P"]]
                  :board-size 1
                  :win-length 2} "P")))
  (is (win? {:type :tictactoe
             :board [["C" "P"]
                     ["P" "C"]]
             :board-size 2
             :win-length 2} "P")))

(deftest computer-move-test
  (is (= [["C"]]
         (:board (game/computer-move
                  {:board [[" "]]
                   :board-size 1
                   :type :tictactoe
                   :computer "C"
                   :status :in-progress}))))
  (is (= [["P"]]
         (:board (game/computer-move
                  {:board [["P"]]
                   :board-size 1
                   :type :tictactoe
                   :computer "C"
                   :status :in-progress})))))
