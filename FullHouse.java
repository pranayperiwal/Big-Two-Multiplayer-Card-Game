/**
 * Creating a hand composed of a Full house.
 *
 * @author Pranay Periwal
 */
public class FullHouse extends Hand{
    /**
     * Instantiates a new Full house hand
     *
     * @param player the player
     * @param card   the cards used to compose a Full House
     */
    public FullHouse(CardGamePlayer player, CardList card)
    {
        super(player,card);
    }

    public boolean isValid(){

        if(this.size()==5){
            int checkingCard1 = this.getCard(0).rank; //stores rank of the first card in hand
            int checkingCard2=0; //stores rank of the card which is not the same as the first card
            int counter1=0, counter2=0; //counters to check which card appears 3 times and which card appears 2 times
            for(int i=0;i<5;i++){
                if(this.getCard(i).rank!=checkingCard1){
                    checkingCard2=this.getCard(i).rank;
                    break;
                }
            }
            for(int i=0;i<5;i++){
                if(this.getCard(i).rank==checkingCard1)
                    counter1++;
                else if(this.getCard(i).rank==checkingCard2)
                    counter2++;
                else
                    return false;
            }
            return (counter1 == 2 && counter2 == 3) || (counter1 == 3 && counter2 == 2);
        }
        else
            return false;

    }

    public String getType(){
        return "FullHouse";
    }
}
