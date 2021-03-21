#File: kadzinga-card-game.py
#Author: Tichaona Kadzinga

from random import randrange

def generatePlayingDeck():
    #Function to generate deck of 40 playing cards
    deck = []
    for i in range(0,10,1):
        for j in range(0,4,1):
            deck.append(i+1)
    return deck


def shuffle(sequence):
    #Implementation of the Fisher-Yates Shuffle Algorithm
    for index in range(0,len(sequence)-1, 1):
        random_index = randrange(len(sequence))
        sequence[index], sequence[random_index] = sequence[random_index], sequence[index]

def drawTopCard(drawPile,discardPile):
    if drawPile:#if there are cards on the drawPile pick top card
        result = drawPile.pop()
    elif discardPile: #if there are no cards shuffle discardPile 
        shuffle(discardPile)
        drawPile = discardPile
        discardPile = []
        result = drawPile.pop()
    else:
        result = None
    return result

def announceCardsDrawn(player1Card, player2Card):
    print('Player 1 ({} cards): {}'.format(len(player1DiscardPile)+len(player1DrawPile)+1,player1Card))
    print('Player 2 ({} cards): {}'.format(len(player2DiscardPile)+len(player2DrawPile)+1,player2Card))

def announceRoundWinner(player):
    if not player:
        print("No winner in this round")    
    elif player == 1:
        print("Player 1 wins this round")
    else:
        print("Player 2 wins this round")

def announceGameWinner(player):
    if not player:
        print("Stalemate !!!!!!!")
    else:
        print('Player {} wins this game!'.format(player))

def isGameOver(player1Card,player2Card):
   return not player1Card or not player2Card 


def whoIsRoundWinner(player1Card,player2Card):
    if player1Card and player2Card:
        if player1Card > player2Card:
            return 1 #Player1 has won the round
        elif player2Card > player1Card:
            return 2 #Player2 has won the round
    elif player1Card and not player2Card:
        return 1 #Player1 has won the round
    elif player2Card and not player1Card:
        return 2
    else:
        return None # It's a tie

def arrangeCardPiles(winner,player1Card,player2Card):
    global player1DiscardPile, player2DiscardPile, tiePile
    #The winner takes both cards onto their discardPile
    #If there is a tie place both cards on the tiePile 
    if not winner:
        tiePile = tiePile + [player1Card,player2Card]
    elif winner == 1:
        player1DiscardPile = player1DiscardPile + [player1Card,player2Card]
        if tiePile:
            player1DiscardPile = player1DiscardPile + tiePile
            tiePile = []
    elif winner == 2:
        player2DiscardPile = player2DiscardPile + [player1Card,player2Card]
        if tiePile:
            player2DiscardPile = player2DiscardPile + tiePile
            tiePile = []

def playATurn():
    global player1DrawPile, player1DiscardPile, player2DrawPile, player2DiscardPile
    player1Card = drawTopCard(player1DrawPile,player1DiscardPile)       
    player2Card = drawTopCard(player2DrawPile,player2DiscardPile)
    winner = whoIsRoundWinner(player1Card,player2Card)
    if isGameOver(player1Card,player2Card):
        announceGameWinner(winner)
        return None
    else:
        announceCardsDrawn(player1Card,player2Card)
        announceRoundWinner(winner)
        arrangeCardPiles(winner,player1Card,player2Card)
        return True

def initialize ():
    global player1DrawPile,player1DiscardPile,player2DrawPile,player2DiscardPile,tiePile,playingDeck
    playingDeck = generatePlayingDeck()
    shuffle(playingDeck)
    player1DrawPile = playingDeck[1::2]
    player2DrawPile = playingDeck[::2]
    player1DiscardPile = []
    player2DiscardPile = []
    tiePile = []
    
if __name__ == "__main__":
    initialize()
    while playATurn():
        playATurn()
    
