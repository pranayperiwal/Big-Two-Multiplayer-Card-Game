/**
 * Creating a hand composed of single card
 *
 * @author Pranay Periwal
 */
public class Single extends Hand{

    /**
     * Instantiates a new Single hand
     *
     * @param player the player
     * @param card   the single card
     */
    public Single(CardGamePlayer player, CardList card)
    {
        super(player,card);
    }

    public boolean isValid(){
        if(this.size()==1)
            return true;
        else
            return false;
    }

    public String getType(){
        return "Single";
    }

}
