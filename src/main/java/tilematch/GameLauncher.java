package tilematch;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

/**
 * Base class for game launchers that handles common functionality.
 */
public abstract class GameLauncher {
    protected static final int WINDOW_WIDTH = 800;
    protected static final int WINDOW_HEIGHT = 600;
    protected Display display;
    protected GameState gameState;
    protected boolean running;
    protected long lastUpdateTime;

    /**
     * Runs the game.
     */
    public void run() {
        initialize();
        gameLoop();
    }

    /**
     * Initializes the game. Must be implemented by subclasses.
     */
    protected abstract void initialize();

    /**
     * The main game loop.
     */
    protected void gameLoop() {
        while (running) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0;
            lastUpdateTime = currentTime;

            gameState.update(deltaTime);
            display.render(gameState);

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles key presses. Must be implemented by subclasses.
     *
     * @param keyCode The key code of the pressed key
     */
    protected abstract void handleKeyPress(int keyCode);

    /**
     * Gets player names from GameChooser.
     * 
     * @return Array containing player 1 and player 2 names
     */
    protected String[] getPlayerNames() {
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
}