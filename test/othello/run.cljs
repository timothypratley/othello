(ns othello.run
  (:require [cljs.test :refer-macros [run-all-tests]]
            [othello.core]
            [othello.game]
            [othello.othello]
            [othello.tictactoe]
            [othello.view]))

(defmethod cljs.test/report [:cljs.test/default :end-run-tests] [m]
  (if (cljs.test/successful? m)
    (println "Success!")
    (println "FAIL")))

(defn run []
  (run-all-tests))
