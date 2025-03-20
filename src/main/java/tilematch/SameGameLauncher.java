package tilematch;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

/**
 * A simple launcher for the Same Game.
 */
public class SameGameLauncher extends GameLauncher {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final String WINDOW_TITLE = "Same Game";

    /**
     * The main entry point for the application.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        SameGameLauncher launcher = new SameGameLauncher();
        launcher.run();
    }

    @Override
    protected void initialize() {
        // Get player names from GameChooser
        String[] playerNames = getPlayerNames();

        display = new Display(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);
        gameState = new SameGameState(10, 10);
        gameState.setDisplay(display);

        // Set player names
        String currPlayer = ((SameGameState) gameState).setPlayerNames(playerNames[0], playerNames[1]);
        ((SameGameState) gameState).setMessage(currPlayer + "'s Turn!");
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
     * Gets player names through dialog boxes.
     * 
     * @return Array containing player 1 and player 2 names
     */
   
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
            case KeyEvent.VK_P:
                gameState.handleInput("P");
                break;
            case KeyEvent.VK_R:
                gameState.handleInput("R");
                break;
            case KeyEvent.VK_C:
                gameState.handleInput("C");
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