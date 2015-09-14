import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel {     // main class for the game   
    // Define constants for the game
    static final String TITLE = "Hello World";        // title of the game
    static final int CANVAS_WIDTH = 800;    // width and height of the drawing canvas
    static final int CANVAS_HEIGHT = 600;
    static final int UPDATES_PER_SEC = 30;    // number of game update per second
    static final long UPDATE_PERIOD_NSEC = 1000000000L / UPDATES_PER_SEC;  // nanoseconds
    // ......

    // Define instance variables for the game objects
    // ......
    // ......

    // Handle for the custom drawing panel
    private GameCanvas canvas;
    private double posY;
    private double posX;
    private double deltaY;
    private double deltaX;
    private int diameter;

    //public static JMenuBar menuBar;    // the menu bar (if needed)

    // Constructor to initialize the UI components and game objects
    public GameMain() {
        // Initialize the game objects
        gameInit();

        // UI components
        canvas = new GameCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        add(canvas);   // center of default BorderLayout

        // Other UI components such as button, score board, if any.
        // ......

        // Set up menu bar
        gameStart();

    }

    // ------ All the game related codes here ------

    // Initialize all the game objects, run only once.
    public void gameInit() {
        posX = CANVAS_WIDTH / 2;
        posY = CANVAS_HEIGHT / 2;
        deltaX = (0.5/4)/UPDATES_PER_SEC;
        deltaY = (0.5/4)/UPDATES_PER_SEC;
        diameter = 30;
    }

    // Start and re-start the game.
    public void gameStart() {
        // Create a new thread
        Thread gameThread =  new Thread() {
            // Override run() to provide the running behavior of this thread.
            @Override
            public void run() {
                gameLoop();
            }
        };
        // Start the thread. start() calls run(), which in turn calls gameLoop().
        gameThread.start();
    }

    // Run the game loop here.
    private void gameLoop() {
        // Regenerate the game objects for a new game
        // ......

        // Game loop
        long beginTime, timeTaken, timeLeft;  // in msec
        while (true) {
            beginTime = System.nanoTime();

                // Update the state and position of all the game objects,
                // detect collisions and provide responses.
                gameUpdate();

            // Refresh the display
            repaint();
            // Delay timer to provide the necessary delay to meet the target rate
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (UPDATE_PERIOD_NSEC - timeTaken) / 1000000L;  // in milliseconds
            if (timeLeft < 10) timeLeft = 10;   // set a minimum
            try {
                // Provides the necessary delay and also yields control so that other thread can do work.
                Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }

    // Shutdown the game, clean up code that runs only once.
    public void gameShutdown() { }

    // One step of the game.
    public void gameUpdate() {
        if(posX < 0 || posX > CANVAS_WIDTH) {
            deltaX *= -1;
            if(posX < 0)
                posX = 0;
            else
                posX = CANVAS_WIDTH;
        }
        if(posY < 0 || posY > CANVAS_HEIGHT) {
            deltaY *= -1;
            if(posY < 0)
                posY = 0;
            else
                posY = CANVAS_HEIGHT;
        }        posX += deltaX;
        posY += deltaY;
    }

    // Refresh the display after each step.
    // Use (Graphics g) as argument if you are not using Java 2D.
    public void gameDraw(Graphics2D g2d) {
        //clear the canvas
        g2d.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        g2d.setColor(Color.RED);
        //draw the ball
        g2d.fillOval((int)posX, (int)posY, diameter, diameter);

    }

    // Process a key-pressed event.
    public void gameKeyPressed(int keyCode) {


        switch (keyCode) {
            case KeyEvent.VK_S:
                deltaX /= 2;
                deltaY /= 2;
                break;
            case KeyEvent.VK_UP:
                deltaY = Math.abs(deltaY) * -2;
                break;
            case KeyEvent.VK_DOWN:
                deltaY = Math.abs(deltaY) * 2;
                break;
            case KeyEvent.VK_LEFT:
                deltaX = Math.abs(deltaX) * -2;
                break;
            case KeyEvent.VK_RIGHT:
                deltaX = Math.abs(deltaX) * 2;
                break;
        }

        posX += deltaX;
        posY += deltaY;

    }

    // Other methods
    // ......

    // Custom drawing panel, written as an inner class.
    class GameCanvas extends JPanel implements KeyListener {
        // Constructor
        public GameCanvas() {
            setFocusable(true);  // so that this can receive key-events
            requestFocus();
            addKeyListener(this);
        }

        // Override paintComponent to do custom drawing.
        // Called back by repaint().
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;  // if using Java 2D
            super.paintComponent(g2d);       // paint background
            setBackground(Color.BLACK);      // may use an image for background

            // Draw the game objects
            gameDraw(g2d);
        }

        // KeyEvent handlers
        @Override
        public void keyPressed(KeyEvent e) {
            gameKeyPressed(e.getKeyCode());
        }

        @Override
        public void keyReleased(KeyEvent e) { }  // not used

        @Override
        public void keyTyped(KeyEvent e) { }     // not used
    }

    // main
    public static void main(String[] args) {
        // Use the event dispatch thread to build the UI for thread-safety.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame(TITLE);
                // Set the content-pane of the JFrame to an instance of main JPanel
                frame.setContentPane(new GameMain());  // main JPanel as content pane
                //frame.setJMenuBar(menuBar);          // menu-bar (if defined)
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null); // center the application window
                frame.setVisible(true);            // show it
            }
        });
    }
}