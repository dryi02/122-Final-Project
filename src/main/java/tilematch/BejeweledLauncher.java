package tilematch;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

/**
 * A simple launcher for the grid demonstration.
 */
public class BejeweledLauncher {
    protected static final int WINDOW_WIDTH = 800;
    protected static final int WINDOW_HEIGHT = 600;
    protected static final String WINDOW_TITLE = "Bejeweled";

    protected Display display;
    protected BejeweledGameState demoState;
    protected boolean running;
    protected long lastUpdateTime;

    /**
     * The main entry point for the application.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        BejeweledLauncher launcher = new BejeweledLauncher();
        launcher.run();
    }

    /**
     * Gets player names through dialog boxes.
     * 
     * @return Array containing player 1 and player 2 names
     */
    private String[] getPlayerNames() {
        // Check if we already have player names
        String[] existingNames = GameChooser.getPlayerNames();
        if (existingNames != null && existingNames[0] != null && existingNames[1] != null) {
            return existingNames;
        }

        // If no existing names, prompt for them
        String player1Name = JOptionPane.showInputDialog(null,
                "Enter Player 1's name:",
                "Player 1",
                JOptionPane.QUESTION_MESSAGE);

        if (player1Name == null || player1Name.trim().isEmpty()) {
            player1Name = "Player 1";
        }

        String player2Name = JOptionPane.showInputDialog(null,
                "Enter Player 2's name:",
                "Player 2",
                JOptionPane.QUESTION_MESSAGE);

        if (player2Name == null || player2Name.trim().isEmpty()) {
            player2Name = "Player 2";
        }

        return new String[] { player1Name, player2Name };
    }

    /**
     * Runs the game.
     */
    public void run() {
        initialize();
        gameLoop();
    }

    /**
     * Initializes the demo.
     */
    protected void initialize() {
        // Get player names from GameChooser
        String[] playerNames = GameChooser.getPlayerNames();

        display = new Display(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);
        demoState = new BejeweledGameState(10, 10);
        demoState.setDisplay(display);

        // Set player names
        demoState.setPlayerNames(playerNames[0], playerNames[1]);

        display.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        running = true;
        lastUpdateTime = System.nanoTime();
    }

    /**
     * The main game loop.
     */
    protected void gameLoop() {
        while (running) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0;
            lastUpdateTime = currentTime;

            demoState.update(deltaTime);
            display.render(demoState);

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles key presses.
     *
     * @param keyCode The key code of the pressed key
     */
    protected void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
                demoState.handleInput("UP");
                break;
            case KeyEvent.VK_DOWN:
                demoState.handleInput("DOWN");
                break;
            case KeyEvent.VK_LEFT:
                demoState.handleInput("LEFT");
                break;
            case KeyEvent.VK_RIGHT:
                demoState.handleInput("RIGHT");
                break;
            case KeyEvent.VK_SPACE:
                demoState.handleInput("SPACE");
                break;
            case KeyEvent.VK_R:
                demoState.handleInput("R");
                break;
            case KeyEvent.VK_M:
                demoState.handleInput("M");
                break;
            case KeyEvent.VK_ESCAPE:
                running = false;
                break;
        }
    }
}