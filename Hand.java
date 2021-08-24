/**
 * The Hand class is a subclass of the CardList class, and is used to model a hand of cards
 *
 * @author Pranay Periwal
 */
public abstract class Hand extends CardList {

    private CardGamePlayer player; //the player who plays this hand.

    /**
     * Instantiates a new Hand with the specified player and list of cards.
     *
     * @param player the player who's getting cards
     * @param cards  the cards for adding to the hand of the player
     */
    public Hand(CardGamePlayer player, CardList cards){
        this.player = player;
        for(int i =0; i<cards.size();i++){
            this.addCard(cards.getCard(i));
        }
    }

    /**
     * a method for retrieving the player of this hand.
     *
     * @return the card game player of the hand
     */
    public CardGamePlayer getPlayer(){
        return player;
    }

    /**
     * a method for retrieving the top card of this hand.
     *
     * @return the top card
     */
    public Card getTopCard(){
        switch (this.size()){
            case 1:{
                return this.getCard(0);
            }
            case 2:{
                if(this.getCard(0).suit>this.getCard(1).suit)
                    return this.getCard(0);
                else
                    return this.getCard(1);
            }
            case 3:{
                int highestSuitIndex=-1; //store index of highest suit card
                int highestSuit=-1; //stores the highest suit
                for(int i =0;i<3;i++){
                    if(this.getCard(i).suit>highestSuit){
                        highestSuit = this.getCard(i).suit;
                        highestSuitIndex=i;
                    }
                }
                return this.getCard(highestSuitIndex);
            }
            case 5:{
                String type = this.getType();

                if(type=="Straight" || type=="Flush" || type=="StraightFlush"){
                    Card mainCheckCard = this.getCard(0); //store the highest card to compare the rest ro
                    for(int i =1;i<5;i++){
                        if(mainCheckCard.compareTo(this.getCard(i)) < 0)
                            mainCheckCard=this.getCard(i);
                    }
                    return mainCheckCard;
                }

                else if(type=="FullHouse"){
                    int tripletRank=0; //store the rank of the triplet
                    Card mainCheckCard = this.getCard(0); //store the highest card to compare the rest to
                    //get the rank of the triplet
                    for(int i=0;i<3;i++){
                        int currentRankCount = 0; //store the count of the current card at ith index
                        int currentRank = this.getCard(i).rank;  //store the rank of the current card
                        for(int j=0;j<5;j++){
                            if(this.getCard(j).rank == currentRank)
                                currentRankCount++;
                        }
                        if(currentRankCount==3){
                            mainCheckCard = this.getCard(i);
                            tripletRank=currentRank;
                            break;
                        }
                    }
                    //for getting the top card
                    for(int i=0;i<5;i++){
                        if(this.getCard(i).rank==tripletRank){
                            if(mainCheckCard.compareTo(this.getCard(i))<0){
                                mainCheckCard=this.getCard(i);
                            }
                        }
                    }
                    return mainCheckCard;

                }
                else {
                    int quadRank=0; //store rank of the quad cards
                    Card mainCheckCard = this.getCard(0); //store card to compare against

                    //get the rank of the quad
                    for(int i =0;i<2;i++){
                        int currentRankCount = 0; //count of the rank for quad
                        int currentRank = this.getCard(i).rank; //current card rank being compared
                        for(int j =0;j<5;j++){
                            if(this.getCard(j).rank==currentRank){
                                currentRankCount++;
                            }
                        }
                        if(currentRankCount==4){
                            mainCheckCard=this.getCard(i);
                            quadRank=currentRank;
                            break;
                        }
                    }
                    //for getting the top card
                    for(int i =0;i<5;i++){
                        if(this.getCard(i).rank==quadRank){
                            if(mainCheckCard.compareTo(this.getCard(i))<0)
                                mainCheckCard=this.getCard(i);
                        }
                    }
                    return mainCheckCard;
                }
            }
            default:{
                return null;
            }


        }
    }

    /**
     * a method for checking if this hand beats a specified hand.
     *
     * @param hand the hand to compare against
     * @return if this hand could beat the specific hand
     */
    public boolean beats(Hand hand){

        if(this.size()!=hand.size() )
            return false;


        if(this.size()==1 || this.size()==2 || this.size() == 3){
            return this.getTopCard().compareTo(hand.getTopCard()) > 0;
        }
        else if(this.size()==5){
            if(this.getType()=="Straight"){
                if(hand.getType()!="Straight")
                    return false;
                return this.getTopCard().compareTo(hand.getTopCard()) > 0;

            }
            else if(this.getType()=="Flush"){
                if(hand.getType()=="Straight")
                    return true;
                else if(hand.getType()=="Flush"){
                    return this.getTopCard().compareTo(hand.getTopCard()) > 0;
                }
                else
                    return false;
            }
            else if(this.getType()=="FullHouse"){
                if(hand.getType()=="Straight"||hand.getType()=="Flush")
                    return true;
                else if(hand.getType()=="FullHouse"){
                    return this.getTopCard().compareTo(hand.getTopCard()) > 0;
                }
                else
                    return false;
            }
            else if(this.getType()=="Quad"){
                if(hand.getType()=="Straight"||hand.getType()=="Flush"||hand.getType()=="FullHouse")
                    return true;
                else if(hand.getType()=="Quad"){
                    return this.getTopCard().compareTo(hand.getTopCard()) > 0;
                }
                else
                    return false;
            }
            else {
                if(hand.getType()=="Straight"||hand.getType()=="Flush"||hand.getType()=="FullHouse"||hand.getType()=="Quad")
                    return true;
                else{
                    return this.getTopCard().compareTo(hand.getTopCard()) > 0;
                }

            }
        }
        else
            return false;
    }

    /**
     * a method for checking if this is a valid hand
     *
     * @return if particular hand could be valid or not
     */
    public abstract boolean isValid();

    /**
     * a method for returning a string specifying the type of this hand.
     *
     * @return the type of the hand
     */
    public abstract String getType();

}
