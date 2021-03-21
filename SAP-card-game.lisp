(defparameter *player1-draw-pile* '())
(defparameter *player1-discard-pile* '())
(defparameter *player2-draw-pile* '())
(defparameter *player2-discard-pile* '())
(defparameter *tie-pile* '())
(defparameter *playing-deck* '())

(defun generate-playing-deck ()
  (loop for i from 1 upto 10
	nconc
	(loop repeat 4
	      collect i)))

(defun shuffle (sequence)
  (loop for i from (length sequence) downto 2
        do (rotatef (elt sequence (random i))
                    (elt sequence (1- i)))
        finally (return sequence)))

(defun initialize ()
  (setf *playing-deck* (shuffle (generate-playing-deck))
	*player1-draw-pile* '()
	*player1-discard-pile* '()
	*player2-draw-pile* '()
	*player2-discard-pile* '()
	*tie-pile* '())
  (loop for i below (length *playing-deck*) by 2
	do
	   (push (elt *playing-deck* i) *player1-draw-pile*)
	   (push (elt *playing-deck* (1+ i)) *player2-draw-pile*)))
  
(defun draw-top-card (player)
  (case player
    (1 
     (cond (*player1-draw-pile*
	    (pop *player1-draw-pile*))
	   (*player1-discard-pile*
	    (progn (setf *player1-draw-pile* (shuffle *player1-discard-pile*)
			 *player1-discard-pile* '())
		   (pop *player1-draw-pile*)))
	   (t nil)))
    (2 
     (cond (*player2-draw-pile*
	    (pop *player2-draw-pile*))
	   (*player2-discard-pile*
	    (progn (setf *player2-draw-pile* (shuffle *player2-discard-pile*)
			 *player2-discard-pile* '())
		   (pop *player2-draw-pile*)))
	   (t nil)))))

(defun announce-cards-drawn(player1-card player2-card)
  (format t "Player 1 (~a cards): ~a ~%" (1+ (apply #'+ (map 'list #'length (list *player1-discard-pile* *player1-draw-pile*)))) player1-card)
  (format t "Player 2 (~a cards): ~a ~%" (1+ (apply #'+ (map 'list #'length (list *player2-discard-pile* *player2-draw-pile*)))) player2-card)) 
    
(defun announce-round-winner(player)
  (format t "~[No winner in~:;Player ~a wins~] this round ~%" player player))
    
(defun announce-game-winner (player)
    (format t "~[Stalemate !!!!!!!~:;Player ~a wins this game!~]~%" player player))
        
(defun is-game-over-p (player1-card player2-card)
   (or (null player1-card)  (null player2-card)))

(defun who-is-round-winner(player1-card player2-card)
  (cond ((and player1-card player2-card)
	 (cond ((> player1-card player2-card)
		1)
	       ((> player2-card player1-card)
		2)
	       (t 0)))
	((and player1-card (null player2-card))
	 1)
	((and player2-card (null player1-card))
	 2)
	(t 0)))    

(defun arrange-card-piles(winner player1-card player2-card)
  ;;The winner takes both cards onto their discardPile
  ;;If there is a tie place both cards on the tiePile
  (case winner
    (0 (setf *tie-pile* (append *tie-pile*  (list player1-card player2-card))))
    (1 (setf *player1-discard-pile* (append *player1-discard-pile* (list player1-card player2-card)))
     (when *tie-pile*
       (setf *player1-discard-pile* (append *player1-discard-pile* *tie-pile*)
	     *tie-pile* '())))
    (2 (setf *player2-discard-pile* (append *player2-discard-pile* (list player1-card player2-card)))
     (when *tie-pile*
       (setf *player2-discard-pile* (append *player2-discard-pile* *tie-pile*)
	     *tie-pile* '())))))
    

(defun play-a-turn ()
  (let* ((player1-card (draw-top-card 1))
	 (player2-card (draw-top-card 2))
	 (winner (who-is-round-winner player1-card player2-card)))
    (if (is-game-over-p player1-card player2-card)
	(progn 
	  (announce-game-winner winner)
	  nil)
	(progn 
          (announce-cards-drawn player1-card player2-card)
          (announce-round-winner winner)
          (arrange-card-piles winner player1-card player2-card)
        t))))


(defun play-a-game ()
  (if (null (play-a-turn))
      nil
      (play-a-game)))
