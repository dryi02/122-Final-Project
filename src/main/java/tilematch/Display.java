package tilematch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

/**
 * The Display class handles the graphical representation of the game.
 * It creates a window and renders the game state.
 */
public class Display {
    private JFrame frame;
    private GamePanel gamePanel;
    private int width;
    private int height;
    private String title;

    /**
     * Creates a new Display with the specified dimensions and title.
     *
     * @param width  The width of the game window
     * @param height The height of the game window
     * @param title  The title of the game window
     */
    public Display(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        createDisplay();
    }

    /**
     * Creates and configures the game window.
     */
    private void createDisplay() {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        gamePanel = new GamePanel(width, height);
        frame.add(gamePanel);
        frame.setVisible(true);

        // Request focus to ensure key events are captured
        frame.requestFocus();
    }

    /**
     * Updates the display with the current game state.
     *
     * @param gameState The current state of the game
     */
    public void render(GameState gameState) {
        gamePanel.setGameState(gameState);
        gamePanel.repaint();
    }

    /**
     * Adds a key listener to the game window.
     *
     * @param keyListener The key listener to add
     */
    public void addKeyListener(KeyListener keyListener) {
        frame.addKeyListener(keyListener);
        // Make sure the frame is focusable to receive key events
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }

    /**
     * Inner class that extends JPanel to render the game.
     */
    private class GamePanel extends JPanel {
        private GameState gameState;

        public GamePanel(int width, int height) {
            setPreferredSize(new Dimension(width, height));
            setBackground(Color.BLACK);
        }

        public void setGameState(GameState gameState) {
            this.gameState = gameState;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Fill the background
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            if (gameState != null) {
                gameState.render(g);
            }
        }
    }
}