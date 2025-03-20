package tilematch;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;

/**
 * BejeweledGameState implements a Bejeweled-style matching game.
 * It extends TileMatchingGameEnvironment to utilize common tile-matching
 * functionality.
 */
public class BejeweledGameState extends TileMatchingGameEnvironment {
    private static final int PLAYER_TIME_LIMIT = 20; // 20 seconds per player
    private static final int GLOBAL_TIME_LIMIT = 300; // 5 minutes in seconds
    private int currentPlayerTime = PLAYER_TIME_LIMIT;
    private int globalTime = GLOBAL_TIME_LIMIT;
    private long lastTimeUpdate = System.currentTimeMillis();
    private boolean isTimerRunning = true;
    private boolean isGameOver = false;

    private String message2 = "Click arrow keys to move selection";

    /**
     * Creates a new BejeweledGameState with the specified dimensions.
     *
     * @param rows    The number of rows in the grid
     * @param columns The number of columns in the grid
     */
    public BejeweledGameState(int rows, int columns) {
        super(rows, columns);
        initializeGrid();
    }

    /**
     * Initializes the grid with random blocks.
     */
    private void initializeGrid() {
        initializeGridWithoutMatches();
        toggleSwapMode();
        // Select the center block initially
        selectedRow = grid.getRows() / 2;
        selectedCol = grid.getColumns() / 2;
    }

    @Override
    public void handleInput(String input) {
        switch (input) {
            case "UP":
                if (selectedRow > 0) {
                    selectedRow--;
                    message2 = "Selected position: (" + selectedRow + ", " + selectedCol + ")";
                }
                break;
            case "DOWN":
                if (selectedRow < grid.getRows() - 1) {
                    selectedRow++;
                    message2 = "Selected position: (" + selectedRow + ", " + selectedCol + ")";
                }
                break;
            case "LEFT":
                if (selectedCol > 0) {
                    selectedCol--;
                    message2 = "Selected position: (" + selectedRow + ", " + selectedCol + ")";
                }
                break;
            case "RIGHT":
                if (selectedCol < grid.getColumns() - 1) {
                    selectedCol++;
                    message2 = "Selected position: (" + selectedRow + ", " + selectedCol + ")";
                }
                break;
            case "SPACE":
                swapBlocks();
                break;
            case "M":
                // Save current stats before returning to menu
                GameChooser.updatePlayerNames(players.get(0).getName(), players.get(1).getName());
                GameChooser.updatePlayerWins(player1Wins, player2Wins);
                // Close the current display
                SwingUtilities.invokeLater(() -> {
                    display.getFrame().dispose();
                    // Launch game chooser in a new thread
                    new Thread(() -> GameChooser.main(new String[0])).start();
                });
                break;
        }
    }

    /**
     * Toggles whether initial matches are allowed when randomizing the grid.
     */
    @Override
    public void randomizeGrid() {
        grid.clear();
        initializeGrid();
        resetAllPlayers();
        currentPlayerTime = PLAYER_TIME_LIMIT;
        globalTime = GLOBAL_TIME_LIMIT;
        isTimerRunning = true;
        isGameOver = false;
        message = getCurrPlayerName() + "'s Turn!";
    }

    /**
     * Clears all blocks from the grid.
     */
    @Override
    public void clearGrid() {
        super.clearGrid();
        message2 = "Grid cleared";
    }

    @Override
    protected void updateGame(double deltaTime) {
        if (isTimerRunning && !isGameOver) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTimeUpdate >= 1000) { // Update every second
                currentPlayerTime--;
                globalTime--;
                lastTimeUpdate = currentTime;

                // Check global timer
                if (globalTime <= 0) {
                    isGameOver = true;
                    message = "Time's Up! Game Over!";
                    message2 = "Player 1 Score: " + players.get(0).getScore() + " | Player 2 Score: "
                            + players.get(1).getScore();
                    isTimerRunning = false;
                    return;
                }

                // Check player timer
                if (currentPlayerTime <= 0) {
                    // Time's up for current player
                    switchPlayers();
                    currentPlayerTime = PLAYER_TIME_LIMIT; // Reset timer for next player
                    lastTimeUpdate = currentTime; // Reset the last update time
                    message = getCurrPlayerName() + "'s Turn!";
                    message2 = "Time's up! Switching players...";
                }
            }
        }
    }

    @Override
    public void switchPlayers() {
        super.switchPlayers();
        // Reset timer for the new player
        currentPlayerTime = PLAYER_TIME_LIMIT;
        lastTimeUpdate = System.currentTimeMillis();
        message = getCurrPlayerName() + "'s Turn!";
    }

    @Override
    protected void renderInstructions(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        int textX = grid.getXOffset() + grid.getColumns() * grid.getCellSize() + 20;
        int textY = grid.getYOffset() + 30;

        g.drawString("Bejeweled", textX, textY);
        g.drawString(message, textX, textY + 30);
        g.drawString(message2, textX, textY + 60);

        // Show both players' scores
        g.drawString(players.get(0).getName() + ": " + players.get(0).getScore(), textX, textY + 90);
        g.drawString(players.get(1).getName() + ": " + players.get(1).getScore(), textX, textY + 120);

        // Show wins
        g.drawString("Wins - " + players.get(0).getName() + ": " + player1Wins + " | " +
                players.get(1).getName() + ": " + player2Wins, textX, textY + 150);

        g.drawString("Player Time: " + currentPlayerTime + "s", textX, textY + 180);
        g.drawString("Game Time: " + (globalTime / 60) + ":" + String.format("%02d", globalTime % 60), textX,
                textY + 210);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Controls:", textX, textY + 240);
        g.drawString("Arrow Keys: Move selection", textX, textY + 260);
        g.drawString("Space: Toggle block/Confirm swap", textX, textY + 280);
        g.drawString("M: Return to Menu", textX, textY + 320);
    }

    @Override
    protected boolean checkGameOver() {
        if ((!hasMatches() && playerOneFinished) || isGameOver) {
            int scoreP1 = players.get(0).getScore();
            int scoreP2 = players.get(1).getScore();

            if (scoreP1 > scoreP2) {
                message = players.get(0).getName() + " Wins!";
                player1Wins++;
            } else if (scoreP2 > scoreP1) {
                message = players.get(1).getName() + " Wins!";
                player2Wins++;
            } else {
                message = "It's a Tie!";
            }
            message2 = players.get(0).getName() + " Score = " + scoreP1 + " | " +
                    players.get(1).getName() + " Score = " + scoreP2;
            // Reset game state after displaying final scores
            playerOneFinished = false;
            isTimerRunning = false;
            resetAllPlayers();
            clearGrid();
            return true;
        }
        return false;
    }
}