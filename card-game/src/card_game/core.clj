(ns card-game.core
  (:gen-class))

(defn generate-card-deck
  "Generate a deck of cards with values 1 to 10 each number appears 4 times"
  []
  (loop [i 0 acc []]
    (if (> i 3 )
      acc
      (recur (inc i) (into acc (range 1 11))))))



(def shuffled-deck (shuffle (generate-card-deck)))

(def player-1-draw-pile (take 20 shuffled-deck))

(def player-2-draw-pile (drop 20 shuffled-deck))

(defn draw-cards
  "Simulate drawing of top card"
  [{player-1-cards :player-1 player-2-cards :player-2}  player-1-discarded-card player-2-discarded-card]
  (let [player-1-top-card (cond
                            (not-empty (:draw-pile player-1-cards))   (first (:draw-pile player-1-cards))
                            (not-empty (:discard-pile player-1-cards)) (first (:discard-pile player-1-cards))
                            :else nil
                            )
        player-2-top-card (cond
                            (not-empty (:draw-pile player-2-cards))   (first (:draw-pile player-2-cards))
                            (not-empty (:discard-pile player-2-cards)) (first (:discard-pile player-2-cards))
                            :else nil)
        player-1-draw-pile (cond
                             (not-empty (:draw-pile player-1-cards))     (rest (:draw-pile player-1-cards))
                             (not-empty (:discard-pile player-1-cards))  (shuffle (:discard-pile player-1-cards))
                             :else ())
        player-2-draw-pile (cond
                             (not-empty (:draw-pile player-2-cards))     (rest (:draw-pile player-2-cards))
                             (not-empty (:discard-pile player-2-cards))  (shuffle (:discard-pile player-2-cards))
                             :else ())
        player-1-discard-pile (into (:discard-pile player-1-cards) player-1-discarded-card )
        player-2-discard-pile (into (:discard-pile player-2-cards) player-2-discarded-card )
        ]
    {:player-1 {:top-card player-1-top-card
                :draw-pile player-1-draw-pile
                :discard-pile player-1-discard-pile}
     :player-2 {:top-card player-2-top-card
                :draw-pile player-2-draw-pile
                :discard-pile player-2-discard-pile}}))

(defn play-a-turn
  "Follow the game rules"
  [player-1-card player-2-card :player-2-played-card]
  (if (game-over? player-1-card (:top-card player-2-cards))
    nil
    (case winner
      (1) {:winner "Player 1 wins"
           :player-1-played-card (:top-card player-1-cards)
           :player-2-played-card (:top-card player-2-cards)})))
:next-cards (draw-cards  )








(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
