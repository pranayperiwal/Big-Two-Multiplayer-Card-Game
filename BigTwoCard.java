/**
 * The BigTwoCard class is a subclass of the Card class, and is used to model a card used in a Big Two card game
 *
 * @author Pranay Periwal
 */
public class BigTwoCard extends Card {

    /**
     * Creates and returns an instance of the Card class.
     *
     * @param suit
     *            an int value between 0 and 3 representing the suit of a card:
     *            <p>
     *            0 = Diamond, 1 = Club, 2 = Heart, 3 = Spade
     * @param rank
     *            an int value between 0 and 12 representing the rank of a card:
     *            <p>
     *            0 = 'A', 1 = '2', 2 = '3', ..., 8 = '9', 9 = '0', 10 = 'J', 11
     *            = 'Q', 12 = 'K'
     */
    public BigTwoCard(int suit, int rank){
        super(suit, rank);
    }

    /**
     * Compares this card with the specified card for order in game BigTwo
     *
     * @param card the card to be compared
     * @return a negative integer, zero, or a positive integer as this card is
     *         less than, equal to, or greater than the specified card
     */
    public int compareTo(Card card){
        int thisCardRank = this.rank; //rank of this card
        int cardRank = card.rank; //rank of card to be compared to
        if(thisCardRank == 0 || thisCardRank == 1){
            thisCardRank = thisCardRank<1 ? 13:14;
        }
        if(cardRank == 0 || cardRank == 1){
            cardRank = cardRank<1 ? 13:14;
        }

        if(thisCardRank>cardRank)
            return 1;
        else if(thisCardRank<cardRank)
            return -1;
        else if(this.suit>card.suit)
            return 1;
        else if(this.suit<card.suit)
            return -1;
        else
            return 0;
    }
}
