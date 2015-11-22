(ns tictactoe.othello
  (:require
   [tictactoe.game :as game :refer [draw? can-move? available? win?]]
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [deftest]]))

(defn new-game [n]
  {:type :othello
   :status :in-progress
   :background-color "green"
   :player "B"
   :computer "W"
   :board-size n
   :board
   (let [x1 (dec (quot n 2))
         y1 (dec (quot n 2))
         x2 (quot n 2)
         y2 (quot n 2)]
     (-> (vec (repeat n (vec (repeat n " "))))
         (assoc-in [x1 y1] "W")
         (assoc-in [x2 y1] "B")
         (assoc-in [x1 y2] "B")
         (assoc-in [x2 y2] "W")))})

(defn line [x y dx dy]
  (iterate (fn [[i j]]
             [(+ i dx) (+ j dy)])
           [(+ x dx) (+ y dy)]))

(deftest line-test
  (is (= [[1 1] [2 2] [3 3]]
         (take 3 (line 0 0 1 1)))))

(defn capture-line [board x y dx dy player]
  (when-let [opponent (game/other-player player)]
    (loop [x (+ x dx)
           y (+ y dy)
           found []]
      (cond
        (= opponent (get-in board [y x]))
        (recur (+ x dx)
               (+ y dy)
               (conj found [x y]))

        (= player (get-in board [y x]))
        found

        :else
        nil))))

(defn capture [board x y player]
  (seq
   (mapcat
    (fn [[dx dy]]
      (capture-line board x y dx dy player))
    (for [dx [-1 0 1]
          dy [-1 0 1]
          :when (not= 0 dx dy)]
      [dx dy]))))

(deftest capture-test
  (is (= [[1 0] [2 0] [3 0]]
         (capture [[" " "W" "W" "W" "B"]] 0 0 "B")))
  (is (= [[1 0] [3 0]]
         (capture [["B" "W" " " "W" "B"]] 2 0 "B")))
  (is (= nil
         (capture [[" " "W" "W" " "]] 0 0 "B"))))

(defn othello-move [board x y flips player]
  (reduce
   (fn [b [i j]]
     (assoc-in b [j i] player))
   (assoc-in board [y x] player)
   flips))

(defmethod can-move? :othello
  [{:keys [status board] :as game} x y player]
  (and (available? game x y)
       (when-let [flips (capture board x y player)]
         (update game :board othello-move x y flips player))))

(defmethod draw? :othello
  [{:keys [computer player] :as game}]
  (and (empty? (game/available-moves game computer))
       (empty? (game/available-moves game player))))

(deftest draw-test
  (is (draw? (assoc (new-game 2)
                    :board [["B" "W"]
                            ["W" "B"]])))
  (is (not (draw? (assoc (new-game 3)
                         :board [["B" "W" " "]
                                 ["W" "B" " "]
                                 [" " " " " "]])))))

(defmethod win? :othello
  [{:keys [board] :as game} player]
  (let [{es " " ws "W" bs "B"} (frequencies (apply concat board))]
    (and (draw? game)
         (if (= player "B")
           (> bs ws)
           (> ws bs)))))

(deftest win-test
  (is (win? {:type :othello
             :board [["B" "B"]
                     ["B" "W"]]}
            "B")))

(deftest computer-moves-twice-to-win
  (let [{:keys [board status]}
        (game/check-game-status
         (assoc (new-game 3)
                :board [["W" "B" " "]
                        [" " " " " "]
                        ["W" "B" " "]]))]
    (is (= [["W" "W" "W"]
            [" " " " " "]
            ["W" "W" "W"]]
           board))
    (is (= :computer-victory
           status))))
