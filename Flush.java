/**
 * Creating a hand composed of a Flush.
 *
 * @author Pranay Periwal
 */
public class Flush extends Hand {
    /**
     * Instantiates a new Flush hand
     *
     * @param player the player
     * @param card   the cards used to compose a Flush
     */
    public Flush(CardGamePlayer player, CardList card)
    {
        super(player,card);
    }

    public boolean isValid(){
        if(this.size()==5){
            for(int i=0;i<4;i++){
                if(this.getCard(i).suit!=this.getCard(i+1).suit){
                    return false;
                }
            }
            return true;
        }
        else
            return false;
    }

    public String getType(){
        return "Flush";
    }
}
