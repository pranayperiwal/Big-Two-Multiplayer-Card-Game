/**
 * Creating a hand composed of a Straight flush.
 *
 * @author Pranay Periwal
 */
public class StraightFlush extends Hand{

    /**
     * Instantiates a new Straight flush hand
     *
     * @param player the player
     * @param card   the cards used to compose a straight flush
     */
    public StraightFlush(CardGamePlayer player, CardList card)
    {
        super(player,card);
    }

    public boolean isValid(){
        if(this.size()==5){
            Straight straight = new Straight(this.getPlayer(), this);
            Flush flush = new Flush(this.getPlayer(), this);
            if(straight.isValid() && flush.isValid())
                return true;
            else
                return false;
        }
        return false;
    }

    public String getType(){
        return "StraightFlush";
    }
}
