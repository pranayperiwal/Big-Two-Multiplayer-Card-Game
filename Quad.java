/**
 * Creating a hand composed of a Quad.
 *
 * @author Pranay Periwal
 */
public class Quad extends Hand {
    /**
     * Instantiates a new Quad hand
     *
     * @param player the player
     * @param card   the cards being used to compose a quad hand
     */
    public Quad(CardGamePlayer player, CardList card)
    {
        super(player,card);
    }

    public boolean isValid(){
        if(this.size()==5){
            for(int i=0;i<2;i++){
                int checkingCard = this.getCard(i).rank; //card to be compared against
                int counter=0; //counter of the same ranking cards
                for(int j=0;j<5;j++){
                    if(this.getCard(j).rank==checkingCard)
                        counter++;
                }
                if(counter==4)
                    return true;
            }
        }
        return false;
    }

    public String getType(){
        return "Quad";
    }
}
