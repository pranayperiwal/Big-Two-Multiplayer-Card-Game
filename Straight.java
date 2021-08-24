/**
 * Creating a hand composed of a Straight.
 *
 * @author Pranay Periwal
 */
public class Straight extends Hand {
    /**
     * Instantiates a new Straight hand
     *
     * @param player the player
     * @param card   the cards being used to compose a straight
     */
    public Straight(CardGamePlayer player, CardList card)
    {
        super(player,card);
    }

    public boolean isValid(){
        if(this.size()==5){
            this.sort();
            int[] currentCardsRanking = new int[5];
            for(int i=0;i<5;i++){
                int currentCardRank = this.getCard(i).rank; //current card rank
                if(currentCardRank == 0 || currentCardRank == 1){
                    currentCardRank = currentCardRank==0 ? 13:14;
                }
                currentCardsRanking[i]=currentCardRank;
            }
            for(int i=0;i<4;i++){
                if(currentCardsRanking[i]+1!=currentCardsRanking[i+1])
                    return false;
            }
            return true;
        }
        else
            return false;


    }

    public String getType(){
        return "Straight";
    }
}
