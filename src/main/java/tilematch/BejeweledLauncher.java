package tilematch;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

/**
 * A simple launcher for the Bejeweled game.
 */
public class BejeweledLauncher extends GameLauncher {
    protected static final int WINDOW_WIDTH = 800;
    protected static final int WINDOW_HEIGHT = 600;
    private static final String WINDOW_TITLE = "Bejeweled";

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
    @Override
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

    @Override
    protected void initialize() {
        // Get player names from GameChooser
        String[] playerNames = getPlayerNames();

        display = new Display(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);
        gameState = new BejeweledGameState(10, 10);
        gameState.setDisplay(display);

        // Set player names
        ((BejeweledGameState) gameState).setPlayerNames(playerNames[0], playerNames[1]);

        display.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        running = true;
        lastUpdateTime = System.nanoTime();
    }

    @Override
    protected void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
                gameState.handleInput("UP");
                break;
            case KeyEvent.VK_DOWN:
                gameState.handleInput("DOWN");
                break;
            case KeyEvent.VK_LEFT:
                gameState.handleInput("LEFT");
                break;
            case KeyEvent.VK_RIGHT:
                gameState.handleInput("RIGHT");
                break;
            case KeyEvent.VK_SPACE:
                gameState.handleInput("SPACE");
                break;
            case KeyEvent.VK_R:
                gameState.handleInput("R");
                break;
            case KeyEvent.VK_M:
                gameState.handleInput("M");
                break;
            case KeyEvent.VK_ESCAPE:
                running = false;
                break;
        }
    }
}