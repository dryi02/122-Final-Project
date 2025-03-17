package tilematch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameChooser {

    private JFrame frame;
    private MenuPanel menuPanel;
    private String[] options = {"Bejeweled", "SameGame"};
    private int selectedIndex = 0;

    public GameChooser() {
        //menu window
        frame = new JFrame("Choose Game");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        //menu panel
        menuPanel = new MenuPanel();
        frame.add(menuPanel);
        frame.setVisible(true);

        //key listener
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                //move up
                selectedIndex = (selectedIndex - 1 + options.length) % options.length;
                menuPanel.repaint();
                break;
            case KeyEvent.VK_DOWN:
                //move down
                selectedIndex = (selectedIndex + 1) % options.length;
                menuPanel.repaint();
                break;
            case KeyEvent.VK_ENTER:
                //start game
                launchGame();
                break;
        }
    }

    private void launchGame() {
        //close window
        frame.dispose();

        //run game on new thread (so it won't block the swing edt)
        new Thread(() -> {
            if (options[selectedIndex].equals("Bejeweled")) {
                BejeweledLauncher.main(new String[0]);
            } else if (options[selectedIndex].equals("SameGame")) {
                SameGameLauncher.main(new String[0]);
            }
        }).start();
    }

    //menu panel
    private class MenuPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //fill background
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            //draw tile
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String title = "Select a Game";
            int titleWidth = g.getFontMetrics().stringWidth(title);
            g.drawString(title, (getWidth() - titleWidth) / 2, 50);

            //draw options
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            for (int i = 0; i < options.length; i++) {
                String text = options[i];
                int textWidth = g.getFontMetrics().stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = 100 + i * 40;

                if (i == selectedIndex) {
                    //select item (yellow)
                    g.setColor(Color.YELLOW);
                    g.drawString("-> " + text, x - 40, y);
                } else {
                    g.setColor(Color.WHITE);
                    g.drawString(text, x, y);
                }
            }
        }
    }

    //main entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameChooser::new);
    }
}
