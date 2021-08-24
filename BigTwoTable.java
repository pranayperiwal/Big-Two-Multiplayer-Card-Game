import com.sun.xml.internal.messaging.saaj.soap.JpegDataContentHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * The Big two table class. It is used to build a GUI for the Big Two card game and handle all user actions.
 *
 * @author Pranay Periwal
 */
public class BigTwoTable implements CardGameTable {

    private final static int MAX_CARD_NUM = 13; // max. no. of cards each player


    private BigTwoClient game; //a card game associated with this table.
    private boolean[] selected; // a boolean array indicating which cards are being selected.
    private boolean[] playersPresent; //a boolean array to track how many players are there
    private int activePlayer; // an integer specifying the index of the active player.
    private JFrame frame; // the main window of the application.
    private JPanel bigTwoPanel; // a panel for showing the cards of each player and the cards played on the table.
    private JPanel cardsPanel; //panel only to hold the cards
    private JButton playButton; // a “Play” button for the active player to play the selected cards.
    private JButton passButton; // a “Pass” button for the active player to pass his/her turn to the next player.
    private JTextArea msgArea; // a text area for showing the current game status as well as end of game messages.
    private JTextArea playerMessagesArea; //text area to display player messages to each other
    private JTextField chatField; //the input for chat
    private Image[][] cardImages; // a 2D array storing the images for the faces of the cards.
    private Image cardBackImage; // an image for the backs of the cards.
    private Image[] avatars; // an array storing the images for the avatars.
    private JMenuBar menuBar; //main menu bar
    private JMenu menuOptions; //holds the game menu options
    private JMenu menuMessage; //holds the menu for messages
    private JMenuItem connect; //menu item to connect to game server
    private JMenuItem quit; //menu item to quit game
    private JMenuItem clearInfo; //menu item to clear game information
    private JMenuItem clearChatBox; //menu item to clear chat box


    /**
     * Instantiates a new Big two table.
     *
     * @param game the big two game
     */
    public BigTwoTable(BigTwoClient game){
        this.game = game;
        this.selected = new boolean[13];
        this.activePlayer = game.getCurrentIdx();
        playersPresent= new boolean[]{false, false, false, false};


        //getting the images
        char[] suits = {'d','c','h', 's'};
        char[] ranks = {'a', '2', '3', '4', '5', '6', '7', '8', '9','t', 'j', 'q', 'k'};
        cardImages = new Image[4][13];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                try {
                    cardImages[i][j] = ImageIO.read(getClass().getResource("images/cards/"+ranks[j]+suits[i]+".gif"));
                } catch(IOException ex){
                    System.out.println("problem! image can't be loaded!");
                }
            }
        }

        //storing the images for the avatars of the players
        try {
            cardBackImage = ImageIO.read(getClass().getResource("images/cards/b.gif"));
            avatars = new Image[4];
            avatars[0] = ImageIO.read(getClass().getResource("images/avatars/ranger-red.png"));
            avatars[1] = ImageIO.read(getClass().getResource("images/avatars/ranger-blue.png"));
            avatars[2] = ImageIO.read(getClass().getResource("images/avatars/ranger-yellow.png"));
            avatars[3] = ImageIO.read(getClass().getResource("images/avatars/ranger-pink.png"));
        } catch (IOException ex) {
            System.out.println("problem! image can't be loaded!");
        }

        this.setup();

    }

    /**
     * Setting up the details about the GUI
     */
    public void setup(){
        //details for the frame
        this.frame = new JFrame();
        frame.setTitle("Big Two Game");
        frame.setDefaultCloseOperation(JFrame. EXIT_ON_CLOSE);


        //details of game panel
        this.bigTwoPanel = new JPanel();
        bigTwoPanel.setBackground(new Color(31,135,70));
        bigTwoPanel.setSize(new Dimension(700, 1000));
        bigTwoPanel.setLayout(new BorderLayout(20, 0));


        //details about the buttons
        playButton = new JButton("Play");
        playButton.addActionListener(new PlayButtonListener());
        passButton = new JButton("Pass");
        passButton.addActionListener(new PassButtonListener());
        JPanel buttonsPanel = new JPanel(); //panel to hold the buttons
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.add(playButton);
        buttonsPanel.add(passButton);


        //details of text area
        JPanel textPanel = new JPanel(); //hold the msgArea and the chatbox
        textPanel.setLayout(new BorderLayout());


        //details about the game information area
        this.msgArea = new JTextArea(20, 50);
        this.msgArea.setLineWrap(true);
        msgArea.setEditable(false);
        msgArea.setFont(msgArea.getFont().deriveFont(16f));
        JScrollPane scroller1 = new JScrollPane(this.msgArea);
        scroller1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);



        JPanel messagesPanel = new JPanel(); //holds the chat box and input
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));

        //details about the chat box
        playerMessagesArea = new JTextArea(20, 50);
        playerMessagesArea.setLineWrap(true);
        playerMessagesArea.setEditable(false);
        playerMessagesArea.setFont(playerMessagesArea.getFont().deriveFont(16f));
        playerMessagesArea.setForeground(Color.BLUE);
        JScrollPane scroller2 = new JScrollPane(this.playerMessagesArea);
        scroller2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //details about the panel holding the label and the input field
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Message: ");
        chatField = new JTextField(30); //TODO need to change
        chatField.setMinimumSize(new Dimension(30, 20));
        chatField.addActionListener(new ChatTextField());
        //adding the label and chat input area
        messageInputPanel.add(label);
        messageInputPanel.add(chatField);

        //adding the chat box and input to messagesPanel
        messagesPanel.add(new Label("Chat Box:"));
        messagesPanel.add(scroller2);
        messagesPanel.add(messageInputPanel);
        messagesPanel.add(new Label(""));

        //adding the chat box and game messages to panel
        textPanel.add(scroller1, BorderLayout.NORTH);
        textPanel.add(messagesPanel, BorderLayout.SOUTH);


        //details about the actual panel for cards
        cardsPanel = new cardsPanel();
        cardsPanel.setBackground(new Color(31,135,70));
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));

        //adding cards panel and buttons panel
        bigTwoPanel.add(cardsPanel, BorderLayout.CENTER);
        bigTwoPanel.add(buttonsPanel, BorderLayout.SOUTH);

        //make the game panel disabled if current player is not active player
        if (game.getCurrentIdx() != activePlayer){
            playButton.setEnabled(false);
            passButton.setEnabled(false);
            cardsPanel.setEnabled(false);
            bigTwoPanel.setEnabled(false);
        } else{
            playButton.setEnabled(true);
            passButton.setEnabled(true);
            cardsPanel.setEnabled(true);
            bigTwoPanel.setEnabled(true);
        }



        //details about Game menu
        menuBar = new JMenuBar();
        menuOptions = new JMenu("Game");
        connect = new JMenuItem("Connect");
        connect.addActionListener(new ConnectMenuItemListener());
        quit = new JMenuItem("Quit");
        quit.addActionListener(new QuitMenuItemListener());
        menuOptions.add(connect);
        menuOptions.add(quit);

        //details about the message menu
        menuMessage = new JMenu("Message");
        clearChatBox = new JMenuItem("Clear Chat Box");
        clearChatBox.addActionListener(new ClearChatBoxMenuItemListener());
        clearInfo = new JMenuItem("Clear Game information");
        clearInfo.addActionListener(new ClearGameInfoMenuItemListener());
        menuMessage.add(clearInfo);
        menuMessage.add(clearChatBox);

        menuBar.add(menuOptions);
        menuBar.add(menuMessage);



        //adding everything to frame
        this.frame.add(this.bigTwoPanel, BorderLayout.CENTER);
        this.frame.add(textPanel, BorderLayout.EAST);
        frame.setJMenuBar(menuBar);
        frame.setSize(1400, 1000);
        frame.setVisible(true);

    }


    /**
     * The Cards panel which is an inner class that extends the JPanel class and implements the MouseListener interface.
     */
    class cardsPanel extends JPanel implements MouseListener {
        /**
         * Instantiates a new Cards panel.
         */
        public cardsPanel(){
            this.addMouseListener(this);
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            int length = game.getPlayerList().get(activePlayer).getNumOfCards()-1;

            //check for all cards minus the last card location
            for (int i = 0; i < length; i++) {
                //if card is already selected
                if(selected[i]){
                    if(( e.getX()>=200+(25*i) && e.getX()<225+(25*i) && e.getY()>=(20+(180*activePlayer)) && e.getY()<=(115+(180*activePlayer)) )|| ( e.getY()>=(20+(180*activePlayer)) && e.getY()<=(35+(180*activePlayer)) && e.getX()>=200+(25*i) && e.getX()<270+(25*i) )){
                        selected[i]=false;
                    }
                }
                //if card is not selected
                else {
                    if(e.getX()>=200+(25*i) &&e.getX()<225+(25*i) && e.getY()>=(35+(180*activePlayer)) && e.getY()<=(130+(180*activePlayer))){
                        selected[i]=true;
                    }

                }
            }
            //check for last card location
            if(length>=0){
                //if card is already selected
                if(selected[length]){
                    if( e.getX()>=200+(25*length) && e.getX()<270+(25*length) && e.getY()>=(20+(180*activePlayer)) && e.getY()<=(115+(180*activePlayer)) ){
                        selected[length]=false;
                    }
                }
                //if card is not selected
                else {
                    if(e.getX()>=200+(25*length) && e.getX()<270+(25*length) && e.getY()>=(30+(180*activePlayer)) && e.getY()<=(130+(180*activePlayer)) ){
                        selected[length]=true;
                    }
                }
            }


            this.repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        public void paintComponent(Graphics g){
            super.paintComponent(g);

            for (int i = 0; i < 4; i++) {
                Graphics2D g2 = (Graphics2D) g;

                if(playersPresent[i]){
                    g.setFont(new Font("TimesRoman", Font.BOLD, 17));
                    //for setting the colour of current player name
                    if(game.getCurrentIdx()==i){
                        g.setColor(Color.YELLOW);

                    } else {
                        g.setColor(Color.BLACK);
                    }
                    if(activePlayer==i ){
                        g.setColor(Color.BLUE);
                        if(game.getCurrentIdx()==i)
                            g.setColor(Color.YELLOW);
                        g.drawString("You", 25 , 20+(180*i));
                    }
                    else {
                        g.setColor(Color.BLACK);
                        if(game.getCurrentIdx()==i)
                            g.setColor(Color.YELLOW);
                        g.drawString(game.getPlayerList().get(i).getName(), 25 , 20+(180*i));
                    }
                    g.setColor(Color.BLACK);
                    g.drawImage(avatars[i], 5, 35+(180*i), this);
                    g2.drawLine(0, 180*(i+1), 1600, 180*(i+1));

                    if(activePlayer==i){
                        CardGamePlayer currentPlayer = game.getPlayerList().get(i);
                        for (int j = 0; j < currentPlayer.getNumOfCards(); j++) {
                            if(selected[j]){
                                g.drawImage(cardImages[currentPlayer.getCardsInHand().getCard(j).getSuit()][currentPlayer.getCardsInHand().getCard(j).getRank()], 200+(j*25), 20+(i*180), this);

                            }
                            else {
                                g.drawImage(cardImages[currentPlayer.getCardsInHand().getCard(j).getSuit()][currentPlayer.getCardsInHand().getCard(j).getRank()], 200+(j*25), 35+(i*180), this);
                            }
                        }

                    }else{
                        for (int j = 0; j < game.getPlayerList().get(i).getNumOfCards(); j++) {
                            g.drawImage(cardBackImage, 200+(j*25), 35+(i*180), this);
                        }
                    }
                }

            }

            //for creating the table
            g.drawString("Table: ", 20, 740);

            if(!game.getHandsOnTable().isEmpty() ){
                g.setColor(Color.red);
                //adds the name of the player who played last
                g.drawString(game.getHandsOnTable().get(game.getHandsOnTable().size()-1).getPlayer().getName(), 80, 740);
                Hand lastPlayed = game.getHandsOnTable().get(game.getHandsOnTable().size()-1);
                //draws the image of the last played hand
                for (int i = 0; i < lastPlayed.size(); i++) {
                    g.drawImage(cardImages[lastPlayed.getCard(i).getSuit()][lastPlayed.getCard(i).getRank()], 200+(i*25), 760, this);

                }
                g.setColor(Color.WHITE);
                if(game.getPlayerList().get(game.getCurrentIdx()).getNumOfCards()-1 == -1){
                    g.drawString(game.getPlayerList().get(game.getCurrentIdx()).getName()+" is the WINNER!", 400, 800);
                }
            }
            repaint();
        }
    }

    /**
     * The Play button listener which is an inner class that implements the ActionListener interface for the Play button.
     */
    class PlayButtonListener implements ActionListener {

        /**
         * Invoked when play button is clicked.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            game.makeMove(activePlayer, getSelected());
            repaint();
        }
    }

    /**
     * The Pass button listener which is an inner class that implements the ActionListener interface for the Pass button.
     */
    class PassButtonListener implements ActionListener{

        /**
         * Invoked when pass button is clicked.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            game.makeMove(game.getCurrentIdx(),null);
            repaint();
        }
    }

    /**
     * The Connect menu item listener which is an an inner class that implements the ActionListener interface for connect menu item.
     */
    class ConnectMenuItemListener implements ActionListener {

        public void actionPerformed(ActionEvent e)
        {

            game.makeConnection();

        }
    }

    /**
     * The type Quit menu item listener.
     */
    class QuitMenuItemListener implements ActionListener {

        /**
         * Invoked when quit menu item is clicked.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * The clear game info menu item listener which is an an inner class that implements the ActionListener interface for clearing game info.
     */
    class ClearGameInfoMenuItemListener implements ActionListener {

        public void actionPerformed(ActionEvent e)
        {

            clearMsgArea();

        }
    }

    /**
     * The clear chat box menu item listener which is an an inner class that implements the ActionListener interface for clearing chat box.
     */
    class ClearChatBoxMenuItemListener implements ActionListener {

        public void actionPerformed(ActionEvent e)
        {

            clearChatMessage();

        }
    }

    /**
     * The Chat text field listener
     */
    class ChatTextField implements ActionListener {

        /**
         * Invoked when an action occurs.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String textToBeSent = chatField.getText();
            if(textToBeSent!=null && !textToBeSent.trim().isEmpty()){
                CardGameMessage message = new CardGameMessage(7, activePlayer, textToBeSent);
                game.sendMessage(message);
            }
            chatField.setText("");
        }
    }

    /**
     * Prints the specified string to the chat box
     *
     * @param message the string to be printed to the chat box
     */
    public void printChatMessage(String message){
        playerMessagesArea.append(message+'\n');
    }

    /**
     * Clears the chat box area of the card game table.
     */
    public void clearChatMessage(){
        playerMessagesArea.setText("");
    }

    /**
     * Sets the index of the active player (i.e., the current player).
     *
     * @param activePlayer an int value representing the index of the active player
     */
    @Override
    public void setActivePlayer(int activePlayer) {
        this.activePlayer=activePlayer;

    }

    /**
     * Returns an array of indices of the cards selected.
     *
     * @return an array of indices of the cards selected
     */
    @Override
    public int[] getSelected() {
        int[] cardIdx = null;
        int count = 0;
        for (int j = 0; j < selected.length; j++) {
            if (selected[j]) {
                count++;
            }
        }

        if (count != 0) {
            cardIdx = new int[count];
            count = 0;
            for (int j = 0; j < selected.length; j++) {
                if (selected[j]) {
                    cardIdx[count] = j;
                    count++;
                }
            }
        }

        if(cardIdx==null){
            cardIdx = new int[]{};
        }
        return cardIdx;
    }


    /**
     * Resets the list of selected cards to an empty list.
     */
    @Override
    public void resetSelected() {
        for (int i = 0; i < 13; i++) {
            selected[i]=false;
        }
    }

    /**
     * Repaints the GUI.
     */
    @Override
    public void repaint() {
        bigTwoPanel.repaint();
        bigTwoPanel.revalidate();

    }

    /**
     * Prints the specified string to the message area of the card game table.
     *
     * @param msg the string to be printed to the message area of the card game
     *            table
     */
    @Override
    public void printMsg(String msg) {
        msgArea.append(msg+'\n');
        msgArea.setCaretPosition(msgArea.getText().length());
    }

    /**
     * Clears the message area of the card game table.
     */
    @Override
    public void clearMsgArea() {
        this.msgArea.setText("");
    }

    /**
     * Resets the GUI.
     */
    @Override
    public void reset() {
        resetSelected();
        game.getHandsOnTable().clear();

        clearMsgArea();
        repaint();

    }

    /**
     * Enables user interactions.
     */
    @Override
    public void enable() {
        playButton.setEnabled(true);
        passButton.setEnabled(true);
        cardsPanel.setEnabled(true);
        bigTwoPanel.setEnabled(true);
    }

    /**
     * Disables user interactions.
     */
    @Override
    public void disable() {
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        cardsPanel.setEnabled(false);
        bigTwoPanel.setEnabled(false);

    }

    /**
     * Sets existence or non existence of players
     */
    public void setExistence(int playerID, boolean exists){
        if(exists){
            playersPresent[playerID]=true;
        }
        else
            playersPresent[playerID]=false;
    }

}
