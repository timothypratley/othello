(ns tictactoe.run
  (:require [cljs.test :refer-macros [run-all-tests]]
            [tictactoe.core]
            [tictactoe.game]
            [tictactoe.othello]
            [tictactoe.tictactoe]
            [tictactoe.view]))

(defmethod cljs.test/report [:cljs.test/default :end-run-tests] [m]
  (if (cljs.test/successful? m)
    (println "Success!")
    (println "FAIL")))

(defn run []
  (run-all-tests))
