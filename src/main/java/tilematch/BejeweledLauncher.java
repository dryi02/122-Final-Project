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