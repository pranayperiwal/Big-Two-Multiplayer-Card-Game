/**
 * Creating a hand composed of a Triple.
 *
 * @author Pranay Periwal
 */
public class Triple extends Hand {
    /**
     * Instantiates a new Triple hand
     *
     * @param player the player
     * @param card   the cards used to compose a Triple
     */
    public Triple(CardGamePlayer player, CardList card)
    {
        super(player,card);
    }

    public boolean isValid(){
        if(this.size()==3 && this.getCard(0).rank==this.getCard(1).rank &&this.getCard(0).rank==this.getCard(2).rank)
            return true;
        else
            return false;
    }

    public String getType(){
        return "Triple";
    }
}