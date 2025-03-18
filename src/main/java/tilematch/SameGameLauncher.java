package tilematch;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

/**
 * A simple launcher for the grid demonstration.
 */
public class SameGameLauncher {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final String WINDOW_TITLE = "Same Game";

    private static Display display;
    private static SameGameState demoState;
    private static boolean running;
    private static long lastUpdateTime;

    /**
     * The main entry point for the application.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        initialize();
        gameLoop();
    }

    /**
     * Gets player names through dialog boxes.
     * 
     * @return Array containing player 1 and player 2 names
     */
    private static String[] getPlayerNames() {
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
     * Initializes the demo.
     */
    private static void initialize() {
        // Get player names from GameChooser
        String[] playerNames = GameChooser.getPlayerNames();

        display = new Display(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);
        demoState = new SameGameState(10, 10);
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
    private static void gameLoop() {
        while (running) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0;
            lastUpdateTime = currentTime;

            // Update state
            demoState.update(deltaTime);

            // Render state
            display.render(demoState);

            // Sleep to limit frame rate
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
    private static void handleKeyPress(int keyCode) {
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
            case KeyEvent.VK_P:
                demoState.handleInput("P");
                break;
            case KeyEvent.VK_R:
                demoState.handleInput("R");
                break;
            case KeyEvent.VK_C:
                demoState.handleInput("C");
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