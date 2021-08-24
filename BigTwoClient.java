import jdk.internal.util.xml.impl.Input;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The BigTwoClient class implements the CardGame interface and NetworkGame interface.
 * It is used to model a Big Two card game that supports 4 players playing over the internet.
 *
 * @author Pranay Sandeep Periwal
 */
public class BigTwoClient implements CardGame,NetworkGame {

    private int numOfPlayers; // an integer specifying the number of players.
    private Deck deck; // a deck of cards
    private ArrayList<CardGamePlayer> playerList; // a list of players.
    private ArrayList<Hand> handsOnTable; //a list of hands played on the table.
    private int playerID; //an integer specifying the playerID (i.e., index) of the local player.
    private String playerName; //a string specifying the name of the local player.
    private String serverIP; // a string specifying the IP address of the game server.
    private int serverPort; // an integer specifying the TCP port of the game server.
    private Socket sock; // a socket connection to the game server.
    private ObjectOutputStream oos; // an ObjectOutputStream for sending messages to the server.
    private ObjectInputStream ois; //an ObjectInputStream for receiving messages from the server.
    private int currentIdx; // an integer specifying the index of the player for the current turn.
    private BigTwoTable table; //a Big Two table which builds the GUI for the game and handles all user actions.
    private Hand previousHand; //maintain the previous Hand being played
    private int passCounter; //maintain a counter for the number of passes made
    private BufferedReader br;


    /**
     * Instantiates a new client.
     */
    public BigTwoClient() {
        playerList = new ArrayList<CardGamePlayer> ();

        for(int i = 0; i < 4;i++)
        {
            CardGamePlayer cgp = new CardGamePlayer();
            playerList.add(cgp);
        }

        numOfPlayers=4;
        handsOnTable = new ArrayList<Hand>();
        table = new BigTwoTable(this);

        table.disable();

        playerName = JOptionPane.showInputDialog("Please enter your name: ", "John Doe");
        if (playerName == null)
        {
            playerName = "NO NAME";
        }
        currentIdx=-1;
        makeConnection();
        table.repaint();

    }


    /**
     * Returns the number of players in this card game.
     *
     * @return the number of players in this card game
     */
    @Override
    public int getNumOfPlayers() {
        return this.playerList.size();
    }

    /**
     * Returns the deck of cards being used in this card game.
     *
     * @return the deck of cards being used in this card game
     */
    @Override
    public Deck getDeck() {
        return this.deck;
    }

    /**
     * Returns the list of players in this card game.
     *
     * @return the list of players in this card game
     */
    @Override
    public ArrayList<CardGamePlayer> getPlayerList() {
        return playerList;
    }

    /**
     * Returns the list of hands played on the table.
     *
     * @return the list of hands played on the table
     */
    @Override
    public ArrayList<Hand> getHandsOnTable() {
        return handsOnTable;
    }

    /**
     * Returns the index of the current player.
     *
     * @return the index of the current player
     */
    @Override
    public int getCurrentIdx() {
        return currentIdx;
    }

    /**
     * Starts the card game.
     *
     * @param deck the deck of (shuffled) cards to be used in this game
     */
    @Override
    public void start(Deck deck) {

        handsOnTable.clear(); //clear hands on table
        previousHand=null; //set previous had to null so that if new game is started, previous hand is cleared

        //remove all the cards held by the players
        for(CardGamePlayer player: playerList ){
            player.removeAllCards();
        }

        //distribute the cards and set player who holds 3 Diamonds as the active player
        this.distributeCards(deck);

        if(playerID!=getCurrentIdx())
            table.disable(); //allow interaction with GUI

        table.repaint();
        table.setActivePlayer(getPlayerID());


    }

    /**
     * Makes a move by the player.
     *
     * @param playerID the playerID of the player who makes the move
     * @param cardIdx
     */
    @Override
    public void makeMove(int playerID, int[] cardIdx) {

        CardGameMessage msg = new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx);
        sendMessage(msg);
    }

    /**
     * Checks the move made by the player.
     *
     * @param playerID the playerID of the player who makes the move
     * @param cardIdx
     */
    @Override
    public synchronized void checkMove(int playerID, int[] cardIdx) {

        //play button pressed
        if(cardIdx!=null) {

            //handle the case of play button pressed but no cards selected
            if(cardIdx.length==0){
                String message = "Not a legal move!!!";
                table.printMsg(message);
                table.repaint();
                return;
            }

            CardList currentCardList = playerList.get(getCurrentIdx()).play(cardIdx); //Cardlist based on input

            //check for the 3 of diamonds in first move
            if (previousHand == null && handsOnTable.isEmpty()) {
                if (!currentCardList.contains(new Card(0, 2))) {
                    String message = currentCardList.toString();
                    message += "  <=== Not a legal move!!!";
                    table.printMsg(message);
                    table.resetSelected();
                    table.repaint();
                    return;
                }
            }

            //composing the hand with the selected cards
            Hand currentHand = composeHand(playerList.get(getCurrentIdx()), currentCardList);

            //if pressed play without any cards
            if (currentHand == null) {
                String message = currentCardList.toString();
                message += "  <=== Not a legal move!!!";
//                String message = "Not a legal move!!!";
                table.printMsg(message);
                table.resetSelected();
                table.repaint();
                return;
            } else {
                if (previousHand != null) {
                    //if there is a previous hand, and new hand does not beat previous hand
                    if (!currentHand.beats(previousHand)) {
                        String message = "{" + currentHand.getType() + "} ";
                        message += currentHand.toString() + " <=== Not a legal move!!!";
                        table.printMsg(message);
                        table.resetSelected();
                        table.repaint();
                        return;
                    }
                }
            }

            //a valid move is made
            handsOnTable.add(currentHand);
            passCounter=0;
            previousHand=currentHand;
            playerList.get(currentIdx).removeCards(currentCardList);
            String message = "{" + currentHand.getType() + "} ";
            message += currentCardList.toString();
            table.printMsg(message);
            table.resetSelected();
            table.repaint();

        }
        //pass button pressed
        else {
            //if 3 passes already made, prevent more passes
            if(previousHand==null) {
                String message = "{pass} <=== Not a legal move!!!";
                table.printMsg(message);
                table.resetSelected();
                table.repaint();
                return;
            }
            passCounter++;
            //if 3 passes done, changes made to prevent more passes
            if(passCounter==3){
                passCounter=0;
                previousHand=null;

            }
            table.printMsg("{pass}");
            table.resetSelected();
            table.repaint();
        }


        //if player has an empty hand, checking win
        if(endOfGame()){
//            currentIdx--;
            table.repaint();
            String gameEnd = "";
            gameEnd+="Game over!\n";

            //winning condition
            for(int i=0;i<4;i++){
                int cardsInHand = playerList.get(i).getNumOfCards();
                if(cardsInHand==0){
                    gameEnd+=playerList.get(i).getName()+ " wins the game.\n";

                } else
                    gameEnd+=playerList.get(i).getName()+" has "+ cardsInHand+" cards in hand.\n";

            }
            table.disable();
            JOptionPane.showMessageDialog(null, gameEnd);
            sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
//            return;
        } else {
            currentIdx=++currentIdx%4;

            table.resetSelected();

            if(this.playerID==currentIdx)
                table.enable();
            else
                table.disable();
            table.repaint();
        }

    }

    /**
     * Checks for end of game.
     *
     * @return true if the game ends; false otherwise
     */
    @Override
    public boolean endOfGame() {
        return playerList.get(currentIdx).getCardsInHand().isEmpty();
    }

    /**
     * Returns the playerID (index) of the local player.
     *
     * @return the playerID (index) of the local player
     */
    @Override
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Sets the playerID (index) of the local player.
     *
     * @param playerID the playerID (index) of the local player.
     */
    @Override
    public void setPlayerID(int playerID) {
        this.playerID = playerID;

    }

    /**
     * Returns the name of the local player.
     *
     * @return the name of the local player
     */
    @Override
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Sets the name of the local player.
     *
     * @param playerName the name of the local player
     */
    @Override
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Returns the IP address of the server.
     *
     * @return the IP address of the server
     */
    @Override
    public String getServerIP() {
        return serverIP;
    }

    /**
     * Sets the IP address of the server.
     *
     * @param serverIP the IP address of the server
     */
    @Override
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     * Returns the TCP port of the server.
     *
     * @return the TCP port of the server
     */
    @Override
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Sets the TCP port of the server
     *
     * @param serverPort the TCP port of the server
     */
    @Override
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Makes a network connection to the server.
     */
    @Override
    public void makeConnection() {
        try{
            setServerIP("127.0.0.1");
            setServerPort(5000);
            sock = new Socket(getServerIP(), getServerPort());
            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(sock.getInputStream());
//           br = new BufferedReader(ois);


        } catch (Exception e){
            System.out.println("connection error");
            e.printStackTrace();
            table.printMsg("Connection failed to server. Please click on 'Options' menu item and click 'Connect'.");
        }

        //creating the receiving message job as an object of the ServerHandler class
        Runnable receivingMessagesJob = new ServerHandler();

        //Creating a thread to receive messages
        Thread receivingMessages = new Thread(receivingMessagesJob);
        receivingMessages.start(); //starting the thread for receiving messages

//        CardGameMessage joinMessage = new CardGameMessage(CardGameMessage.JOIN, -1, this.getPlayerName());
//        sendMessage(joinMessage); //sending message to JOIN

//        sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null)); //sending message READY

        table.repaint();

    }

    /**
     * Parses the specified message received from the server.
     *
     * @param message the specified message received from the server
     */
    @Override
    public synchronized void parseMessage(GameMessage message) {
        
        switch (message.getType()){

        //message of type PLAYER_LIST
            case 0: {
                setPlayerID(message.getPlayerID());
                table.setActivePlayer(getPlayerID());
                //setting the player names received as data
                String[] playerNames = (String []) message.getData();
                for (int i = 0; i < getNumOfPlayers(); i++) {
                    if(playerNames[i]!=null){
                        this.playerList.get(i).setName(playerNames[i]);
                        table.setExistence(i, true);

                    }
                    else {
                        table.setExistence(i, false);
                    }
                }
                CardGameMessage joinMessage = new CardGameMessage(CardGameMessage.JOIN, -1, this.getPlayerName());
                sendMessage(joinMessage); //sending message to JOIN
            }
            break;
            //message of type JOIN
            case 1: {
                playerList.get(message.getPlayerID()).setName((String)message.getData());
                table.setExistence(message.getPlayerID(), true);
                table.printMsg(message.getData()+" has been added.");
                if(playerID==message.getPlayerID()){
                    sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null)); //message of type READY
                }
                numOfPlayers++;
                table.repaint();

            }
            break;

            //message of type FULL
            case 2: {
                table.printMsg("The server is now full and player cannot join.");
                table.repaint();
            }
            break;

            //message of type QUIT
            case 3: {
                table.printMsg(getPlayerList().get(getPlayerID()).getName()+" is quitting the game.");
                getPlayerList().get(message.getPlayerID()).setName("");
                table.setExistence(message.getPlayerID(), false);
                if(!endOfGame()){
                    table.disable();
                    for (int i = 0; i < 4; i++)
                    {
                        playerList.get(i).removeAllCards();
                    }
                    sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));

                }
                table.repaint();

            }
            break;

            //message of type READY
            case 4: {
                table.printMsg(getPlayerList().get(message.getPlayerID()).getName()+" is ready.");
                table.repaint();
            }
            break;

            //message of type START
            case 5: {
                table.enable();
                table.clearMsgArea();
                table.clearChatMessage();
                deck = (BigTwoDeck) message.getData();
                start(deck);
                table.repaint();
                table.printMsg("All players are ready. Starting new game.");
                if(playerID==getCurrentIdx()){
                    table.printMsg("Your turn:");
                }
                else{
                    table.printMsg(playerList.get(getCurrentIdx()).getName()+"'s turn:");
                }

            }
            break;

            //message of type MOVE
            case 6:{
                checkMove(message.getPlayerID(), (int[]) message.getData());
                if(playerID==getCurrentIdx()){
                    table.printMsg("Your turn:");
                }
                else{
                    table.printMsg(playerList.get(getCurrentIdx()).getName()+"'s turn:");
                }
                table.repaint();
            }
            break;

            //message of type MSG
            case 7: {
                table.printChatMessage((String)message.getData());
                table.repaint();
            }
            break;

            default:{
                table.printMsg("Wrong message type: "+ message.getType());
                table.repaint();
            }
        }
    }

    /**
     * Sends the specified message to the server.
     *
     * @param message the specified message to be sent the server
     */
    @Override
    public void sendMessage(GameMessage message) {
        try {
            oos.writeObject(message);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Distribute shuffled cards to the players.
     *
     * @param deck the BigTwoDeck shuffled deck of cards
     */
    public void distributeCards(Deck deck){
        //distributing the cards
        for(int i=0;i<4;i++){
            for(int j =0;j<13;j++){
                Card currentCard = deck.getCard(0); //get 1 card to be distributed from deck
                //setting the current and active player
                if(currentCard.rank==2 && currentCard.suit==0) {
                    currentIdx = i;
                }

                playerList.get(i).addCard(currentCard);
                deck.removeCard(0);
            }
            playerList.get(i).sortCardsInHand();
        }
    }


    /**
     * a method for creating a BigTwoClient instance
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        BigTwoClient client = new BigTwoClient();
    }


    /**
     * a method for returning a valid hand from the specified list of cards of the player.
     *
     * @param player the current player
     * @param cards  the cards current player decided to pick
     * @return the hand
     */
    public static Hand composeHand(CardGamePlayer player, CardList cards){
        StraightFlush straightflush = new StraightFlush(player,cards); // declaring a new straightflush hand
        Quad quad = new Quad(player,cards); // declaring a new quad hand
        FullHouse fullhouse = new FullHouse(player,cards); // declaring a new fullhouse hand
        Flush flush = new Flush(player,cards); // declaring a new flush hand
        Straight straight = new Straight(player,cards); // declaring a new straight hand
        Triple triple = new Triple(player,cards); // declaring a new triple hand
        Pair pair = new Pair(player,cards); // declaring a new pair hand
        Single single = new Single(player,cards); // declaring a new single hand
        if(straightflush.isValid())
            return straightflush;

        else if(quad.isValid())
            return quad;

        else if(fullhouse.isValid())
            return fullhouse;

        else if(flush.isValid())
            return flush;

        else if(straight.isValid())
            return straight;

        else if(triple.isValid())
            return triple;

        else if(pair.isValid())
            return pair;

        else if(single.isValid())
            return single;
        else
            return null;

    }


    /**
     * Implements the Runnable interface for the server
     */
    class ServerHandler implements Runnable{

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            CardGameMessage message;

            try{
//                System.out.println(br.readLine());
                //receive messages from the server
                while ((message = (CardGameMessage) ois.readObject()) != null){
                    parseMessage(message);
                }
            }
            catch (Exception exception){
                exception.printStackTrace();
            }

            table.repaint();
        }
    }
}
