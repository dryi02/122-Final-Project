package tilematch;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
        display = new Display(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);
        demoState = new BejeweledGameState(10, 10);

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
        case KeyEvent.VK_ESCAPE:
            running = false;
            break;
        }
    }
}