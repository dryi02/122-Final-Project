package tilematch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameChooser {

    private JFrame frame;
    private MenuPanel menuPanel;
    private String[] options = { "Bejeweled", "SameGame" };
    private int selectedIndex = 0;
    private static String player1Name = null;
    private static String player2Name = null;
    private static int player1Wins = 0;
    private static int player2Wins = 0;

    public GameChooser() {
        // Check if we need to get player names
        if (player1Name == null || player2Name == null) {
            initializePlayerNames();
        }

        // menu window
        frame = new JFrame("Choose Game");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // menu panel
        menuPanel = new MenuPanel();
        frame.add(menuPanel);
        frame.setVisible(true);

        // key listener
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    private void initializePlayerNames() {
        String p1Name = JOptionPane.showInputDialog(null,
                "Enter Player 1's name:",
                "Player 1",
                JOptionPane.QUESTION_MESSAGE);

        if (p1Name == null || p1Name.trim().isEmpty()) {
            p1Name = "Player 1";
        }

        String p2Name = JOptionPane.showInputDialog(null,
                "Enter Player 2's name:",
                "Player 2",
                JOptionPane.QUESTION_MESSAGE);

        if (p2Name == null || p2Name.trim().isEmpty()) {
            p2Name = "Player 2";
        }

        player1Name = p1Name;
        player2Name = p2Name;
    }

    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                // move up
                selectedIndex = (selectedIndex - 1 + options.length) % options.length;
                menuPanel.repaint();
                break;
            case KeyEvent.VK_DOWN:
                // move down
                selectedIndex = (selectedIndex + 1) % options.length;
                menuPanel.repaint();
                break;
            case KeyEvent.VK_ENTER:
                // start game
                launchGame();
                break;
        }
    }

    private void launchGame() {
        // close window
        frame.dispose();

        // run game on new thread (so it won't block the swing edt)
        new Thread(() -> {
            if (options[selectedIndex].equals("Bejeweled")) {
                BejeweledLauncher.main(new String[0]);
            } else if (options[selectedIndex].equals("SameGame")) {
                SameGameLauncher.main(new String[0]);
            }
        }).start();
    }

    // menu panel
    private class MenuPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // fill background with gradient
            GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 0, 50), 0, getHeight(), new Color(0, 0, 100));
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // draw title with shadow
            g.setColor(new Color(0, 0, 0, 100));
            g.setFont(new Font("Arial", Font.BOLD, 26));
            String title = "Select a Game";
            int titleWidth = g.getFontMetrics().stringWidth(title);
            g.drawString(title, (getWidth() - titleWidth) / 2 + 2, 52);

            g.setColor(Color.WHITE);
            g.drawString(title, (getWidth() - titleWidth) / 2, 50);

            // draw options with glow effect
            g.setFont(new Font("Arial", Font.BOLD, 20));
            for (int i = 0; i < options.length; i++) {
                String text = options[i];
                int textWidth = g.getFontMetrics().stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = 100 + i * 40;

                if (i == selectedIndex) {
                    // selected item (yellow with glow)
                    g.setColor(new Color(255, 255, 0, 100));
                    g.drawString("-> " + text, x - 40, y);
                    g.setColor(Color.YELLOW);
                    g.drawString("-> " + text, x - 40, y);
                } else {
                    g.setColor(Color.WHITE);
                    g.drawString(text, x, y);
                }
            }

            // draw player stats with background
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 220, getWidth(), 40);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(Color.CYAN);
            String stats = player1Name + ": " + player1Wins + " wins | " +
                    player2Name + ": " + player2Wins + " wins";
            int statsWidth = g.getFontMetrics().stringWidth(stats);
            g.drawString(stats, (getWidth() - statsWidth) / 2, 245);

            // draw controls in a box
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(20, 270, getWidth() - 40, 60);
            g.setColor(new Color(200, 200, 200));
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Controls:", 40, 290);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            String[] controls = {
                    "Arrow Keys: Navigate menu",
                    "ENTER: Select game",
            };
            int controlY = 310;
            for (String control : controls) {
                g.drawString(control, 40, controlY);
                controlY += 15;
            }
        }
    }

    // main entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameChooser::new);
    }

    // Static methods to update player stats
    public static void updatePlayerNames(String p1Name, String p2Name) {
        player1Name = p1Name;
        player2Name = p2Name;
    }

    public static void updatePlayerWins(int p1Wins, int p2Wins) {
        player1Wins = p1Wins;
        player2Wins = p2Wins;
    }

    public static String[] getPlayerNames() {
        return new String[] { player1Name, player2Name };
    }

    public static int[] getPlayerWins() {
        return new int[] { player1Wins, player2Wins };
    }
}
