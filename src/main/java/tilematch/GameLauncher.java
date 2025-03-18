package tilematch;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
        return GameChooser.getPlayerNames();
    }
}