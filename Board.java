import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;


public class Board extends JPanel implements ActionListener, KeyListener {

    // controls the delay between each tick in ms
    private final int DELAY = 25;
    // controls the size of the board
    public static final int TILE_SIZE = 60;
    public static final int ROWS = 12;
    public static final int COLUMNS = 18;
    
    //Color(61, 131, 97)
    public static final int BOARD_COL_A_R = 61;
    public static final int BOARD_COL_A_G = 131;
    public static final int BOARD_COL_A_B = 97;

    //Color(30, 201, 139)
    public static final int BOARD_COL_B_R = 30;
    public static final int BOARD_COL_B_G = 201;
    public static final int BOARD_COL_B_B = 139;
    
    // controls how many coins appear on the board
    public static final int NUM_COINS = 5;
    public static final int COIN_VAL = 50;
    public static final int COIN_DIMENSIONS = 60;
    // suppress serialization warning
    private static final long serialVersionUID = 490905409104883233L;
    
    // keep a reference to the timer object that triggers actionPerformed() in
    // case we need access to it in another method
    private Timer timer;
    // objects that appear on the game board
    private Player player;
    private ArrayList<Coin> coins = new ArrayList<Coin>();

    //variables for background image
    private BufferedImage initialImage;
    private Image image;

    //Creating variables for time
    public double seconds = 0;

    public Board() {
        //Calling background image to load
        loadImage();


        // set the game board size
        setPreferredSize(new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS));
        // set the game board background color
        setBackground(new Color(BOARD_COL_B_R, BOARD_COL_B_G, BOARD_COL_B_B));

        // initialize the game state
        player = new Player();
        populateCoins(NUM_COINS);

        // this timer will call the actionPerformed() method every DELAY ms
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // this method is called by the timer every DELAY ms.
        // use this space to update the state of your game or animation
        // before the graphics are redrawn.

        // prevent the player from disappearing off the board
        player.tick();

        // give the player points for collecting coins
        collectCoins();

        // calling repaint() will trigger paintComponent() to run again,
        // which will refresh/redraw the graphics.
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // when calling g.drawImage() we can use "this" for the ImageObserver 
        // because Component implements the ImageObserver interface, and JPanel 
        // extends from Component. So "this" Board instance, as a Component, can 
        // react to imageUpdate() events triggered by g.drawImage()

        // draw our graphics.
        drawBackground(g);
        drawScore(g);
        for (Coin coin : coins) {
            coin.draw(g, this);
        }
        player.draw(g, this);

        // this smooths out animations on some systems
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // this is not used but must be defined as part of the KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // react to key down events
        player.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

    private void drawBackground(Graphics g) {
        // draw a checkered background
        g.setColor(new Color(BOARD_COL_B_R, BOARD_COL_B_G, BOARD_COL_B_B));
        g.drawImage(image, 0, 0, null);
        
        /*
        g.setColor(new Color(BOARD_COL_A_R, BOARD_COL_A_G, BOARD_COL_A_B));
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                // only color every other tile
                if ((row + col) % 2 == 1) {
                    // draw a square tile at the current row/column position
                    g.fillRect(
                        col * TILE_SIZE, 
                        row * TILE_SIZE, 
                        TILE_SIZE, 
                        TILE_SIZE
                    );
                }
            }    
        }
        */
    }

    private void loadImage() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            //image = ImageIO.read(new File("images/coin.png"));
            initialImage = ImageIO.read(new File("images/background.png"));
        
        
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
        //Resize image  
        image = initialImage.getScaledInstance(TILE_SIZE*COLUMNS, TILE_SIZE*ROWS, Image.SCALE_SMOOTH);
    }

    private void drawScore(Graphics g) {
        // set the text to be displayed
        String text = "$" + player.getScore();
        
        // we need to cast the Graphics to Graphics2D to draw nicer text
        Graphics2D g2d = (Graphics2D) g;        
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // set the text color and font
        g2d.setColor(new Color(255,255,255));
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        // draw the score in the bottom center of the screen
        // https://stackoverflow.com/a/27740330/4655368
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // the text will be contained within this rectangle.
        // here I've sized it to be the entire bottom row of board tiles
        Rectangle rect = new Rectangle(0, TILE_SIZE * (ROWS - 1), TILE_SIZE * COLUMNS, TILE_SIZE);
        // determine the x coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // determine the y coordinate for the text
        // (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // draw the string
        g2d.drawString(text, x, y);

        //Defining Timer variables:
        //Variable for time display
        //Clock increments. Increases seconds by 0.025 milliseconds.
        seconds += 0.04;

        //Decimal Format for time:
        DecimalFormat displayDF = new DecimalFormat("#.##");
        seconds = new Double(displayDF.format(seconds));

        String timeDisplay = "Time: " + seconds;

        //Displaying the timer
        g2d.drawString(timeDisplay, 475, 670);

    }

    private void populateCoins(int number) {
        // ArrayList<Coin> tempCoins = coins;
        Random rand = new Random();

        // create the given number of coins in random positions on the board.
        // note that there is not check here to prevent two coins from occupying the same
        // spot, nor to prevent coins from spawning in the same spot as the player
        for (int i = 0; i < number; i++) {
            int coinX = rand.nextInt(COLUMNS);
            int coinY = rand.nextInt(ROWS);
            coins.add(new Coin(coinX, coinY));
        }

        // Random coin variable to determine random coins being added
        Random chance = new Random();

        //There is a 1 in 30 chance of the variable being 1. If it equals 1, add a coin.
         
        if(chance.nextInt(30) == 1){
            populateCoins(1);
        }

        //There is a 1 in 50 of the chance variable being 2. If it equals 2, remove a coin.
        if(chance.nextInt(20) == 2){
            coins.remove(0);
        }
         


    }

    private void collectCoins() {
        // allow player to pickup coins
        ArrayList<Coin> collectedCoins = new ArrayList<>();
        for (Coin coin : coins) {
            // if the player is on the same tile as a coin, collect it
            if (player.getPos().equals(coin.getPos())) {
                // give the player some points for picking this up
                player.addScore(COIN_VAL);
                collectedCoins.add(coin);
            }
        }
        // remove collected coins from the board
        coins.removeAll(collectedCoins);
        if (!collectedCoins.isEmpty()){
            populateCoins(1);
        }
    }

}
