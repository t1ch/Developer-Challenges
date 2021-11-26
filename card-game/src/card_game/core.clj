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

(def initial-card-state {:player-1  {:top-card (first player-1-draw-pile)
                                    :draw-pile (rest player-1-draw-pile)
                                    :discard-pile ()}
                         :player-2  {:top-card (first player-2-draw-pile)
                                     :previous-top-card ()
                                     :draw-pile (rest player-2-draw-pile)
                                     :discard-pile ()}
                         :tie-pile ()})

(defn draw-cards
  "Simulate drawing of top card"
  [{player-1-cards :player-1 player-2-cards :player-2 tie-pile :tie-pile}]
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
        player-1-discard-pile
        (if (> (:top-card player-1-cards) (:top-card player-2-cards))
          (if (not-empty tie-pile)
            (into (:discard-pile player-1-cards) (into tie-pile (list (:top-card player-1-cards) (:top-card player-2-cards))))
            (into (:discard-pile player-1-cards) (list (:top-card player-1-cards) (:top-card player-2-cards)))
            )
          (:discard-pile player-1-cards))

        player-2-discard-pile
        (if (> (:top-card player-2-cards) (:top-card player-1-cards))
          (if (not-empty tie-pile)
            (into (:discard-pile player-2-cards) (into tie-pile (list (:top-card player-1-cards) (:top-card player-2-cards))))
            (into (:discard-pile player-2-cards) (list (:top-card player-1-cards) (:top-card player-2-cards)))
            )
          (:discard-pile player-2-cards))

        new-tie-pile
        (cond
          (= (:top-card player-1-cards) (:top-card player-2-cards) ) (into tie-pile (list (:top-card player-1-cards) (:top-card player-2-cards)))
          (and (not=  (:top-card player-1-cards) (:top-card player-2-cards)) (not-empty tie-pile)) ()
          :else tie-pile)
        ]
    (if (or (nil? player-1-top-card) (nil? player-2-top-card) (empty? player-1-draw-pile) (empty? player-2-draw-pile))
      ()
      (let   [current-state {:player-1 {:top-card player-1-top-card,
                                        :draw-pile player-1-draw-pile
                                        :discard-pile player-1-discard-pile}
                             :player-2 {:top-card player-2-top-card
                                        :draw-pile player-2-draw-pile
                                        :discard-pile player-2-discard-pile}
                             :tie-pile new-tie-pile}]
        (cons current-state (draw-cards current-state))))))

(defn round-winner [player-1-card player-2-card]
  (cond (= player-1-card player-2-card) "No winner in"
        (> player-1-card player-2-card)  "Player 1 wins"
        :else "Player 2 wins"))

(defn announce-game-winner [{player-1-cards :player-1 player-2-cards :player-2 tie-pile :tie-pile}]
  (let [player-1-discard-pile-count (count (:discard-pile player-1-cards))
        player-2-discard-pile-count (count (:discard-pile player-2-cards))]
    (let [winner (cond
                   (= player-1-discard-pile-count player-2-discard-pile-count) "Stalemate !!!!!!!"
                   (> player-1-discard-pile-count player-2-discard-pile-count) "Player 1 wins the game!"
                   :else "Player 2 wins the game!")]
      (println winner))))

(defn announce-round
  [{player-1-cards :player-1 player-2-cards :player-2 tie-pile :tie-pile}]
  (println (format  "Player 1 (%d cards): %d " (inc (apply + (map count (list (:draw-pile player-1-cards) (:discard-pile player-1-cards))))) (:top-card player-1-cards)))
  (println (format  "Player 2 (%d cards): %d " (inc (apply + (map count (list (:draw-pile player-2-cards) (:discard-pile player-2-cards))))) (:top-card player-2-cards)))
  (let [winner (round-winner (:top-card player-1-cards) (:top-card player-2-cards))]
    (println (str winner " this round"))))

(defn play-cards [initial-card-state]
  (let [all-cards (cons initial-card-state (draw-cards initial-card-state))]
    (doseq [card all-cards]
      (announce-round card))
    (announce-game-winner (last all-cards))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (play-cards initial-card-state))
