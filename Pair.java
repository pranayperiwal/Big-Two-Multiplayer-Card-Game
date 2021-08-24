/**
 * Creating a hand composed of a Pair.
 *
 * @author Pranay Periwal
 */
public class Pair extends Hand{

    /**
     * Instantiates a new Pair hand
     *
     * @param player the player
     * @param card   the card
     */
    public Pair(CardGamePlayer player, CardList card)
    {
        super(player,card);
    }

    public boolean isValid(){
        if(this.size()==2 && this.getCard(0).rank==this.getCard(1).rank)
                return true;
        else
            return false;
    }

    public String getType(){
        return "Pair";
    }
}
