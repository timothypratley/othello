(ns othello.game)

(defmulti can-move? (fn [game i j player] (:type game)))
(defmulti win? (fn [game player] (:type game)))
(defmulti draw? :type)

(def other-player
  {"W" "B"
   "B" "W"
   "C" "P"
   "P" "C"})

(defn game-status [{:keys [computer player] :as game}]
  (cond
    (win? game player) :player-victory
    (win? game computer) :computer-victory
    (draw? game) :draw
    :else :in-progress))

(defn update-status [game]
  (assoc game :status (game-status game)))

(defn available? [{:keys [status board]} i j]
  (and (= status :in-progress)
       (= (get-in board [j i]) " ")))

(defn available-moves [{:keys [board-size] :as game} player]
  (filter
   identity
   (for [i (shuffle (range board-size))
         j (shuffle (range board-size))]
     (can-move? game i j player))))

(defn computer-move [{:keys [computer] :as game}]
  (or
   (first (available-moves game computer))
   game))

(defn check-game-status [{:keys [player computer] :as game}]
  (let [g (update-status (or (computer-move game) game))]
    (if (and (empty? (available-moves g player))
             (seq (available-moves g computer)))
      (check-game-status g)
      g)))

(defn player-move [game i j]
  (let [{:keys [board board-size player]} @game
        g (can-move? @game i j player)]
    (when g
      (reset! game g)
      (if (win? @game player)
        (swap! game assoc :status :player-victory)
        (swap! game check-game-status)))))
