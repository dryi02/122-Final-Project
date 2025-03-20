package tilematch;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 * SameGameState implements a Same Game style matching game.
 * It extends TileMatchingGameEnvironment to utilize common tile-matching
 * functionality.
 */
public class SameGameState extends TileMatchingGameEnvironment {
    private String message2 = "Turns: " + getCurrPlayerScore();
    private Grid gridSave;

    /**
     * Creates a new SameGameState with the specified dimensions.
     */
    public SameGameState(int rows, int columns) {
        super(rows, columns);
        gridSave = new Grid(rows, columns);
        initializeGrid();
    }

    /**
     * Initializes the grid with random blocks.
     */
    private void initializeGrid() {
        initializeGridWithMatches();
        // Select the center block initially
        selectedRow = grid.getRows() / 2;
        selectedCol = grid.getColumns() / 2;

        // Save initial grid state
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                Block block = grid.getBlock(row, col);
                if (block != null) {
                    gridSave.placeBlock(new Block(block.getRow(), block.getColumn(), block.getType(), block.getColor()),
                            row, col);
                }
            }
        }
    }

    /**
     * Pops (removes) all connected blocks of the same color at the selected
     * position.
     * In SameGame, we can pop any number of connected blocks (no minimum
     * requirement).
     */
    @Override
    protected void popConnectedBlocks() {
        if (!grid.isOccupied(selectedRow, selectedCol)) {
            message2 = "No block to pop at (" + selectedRow + ", " + selectedCol + ")";
            return;
        }

        Block selectedBlock = grid.getBlock(selectedRow, selectedCol);
        Color targetColor = selectedBlock.getColor();

        // Find all connected blocks of the same color using BFS
        Set<Point> connectedBlocks = grid.findConnectedBlocks(selectedRow, selectedCol, targetColor);

        // In SameGame, we can pop any number of connected blocks
        if (connectedBlocks.size() >= 1) {
            // Remove all connected blocks
            for (Point p : connectedBlocks) {
                grid.removeBlock(p.x, p.y);
            }

            // Update score and message
            addCurrPlayerScore(1);
            message = "Popped " + connectedBlocks.size() + " blocks! Turns: " + getCurrPlayerScore();
            message2 = "Turns: " + getCurrPlayerScore();

            // Apply gravity to make blocks fall
            grid.applyGravity();
        }
    }

    @Override
    public void handleInput(String input) {
        switch (input) {
            case "UP":
                if (selectedRow > 0) {
                    selectedRow--;
                    message = "Selected position: (" + selectedRow + ", " + selectedCol + ")";
                }
                break;
            case "DOWN":
                if (selectedRow < grid.getRows() - 1) {
                    selectedRow++;
                    message = "Selected position: (" + selectedRow + ", " + selectedCol + ")";
                }
                break;
            case "LEFT":
                if (selectedCol > 0) {
                    selectedCol--;
                    message = "Selected position: (" + selectedRow + ", " + selectedCol + ")";
                }
                break;
            case "RIGHT":
                if (selectedCol < grid.getColumns() - 1) {
                    selectedCol++;
                    message = "Selected position: (" + selectedRow + ", " + selectedCol + ")";
                }
                break;
            case "P":
                popConnectedBlocks();
                checkSwitchPlayer();
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

    @Override
    public void randomizeGrid() {
        grid.clear();
        initializeGrid();
        message = getCurrPlayerName() + "'s Turn!";
        message2 = "Turns: " + getCurrPlayerScore() + " | Wins - " +
                players.get(0).getName() + ": " + player1Wins + " | " +
                players.get(1).getName() + ": " + player2Wins;
    }

    /**
     * Clears all blocks from the grid.
     */
    @Override
    public void clearGrid() {
        super.clearGrid();
        message = "Grid cleared";
    }

    @Override
    protected void updateGame(double deltaTime) {
        // No game logic to update in this game
    }

    @Override
    protected void renderInstructions(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        int textX = grid.getXOffset() + grid.getColumns() * grid.getCellSize() + 20;
        int textY = grid.getYOffset() + 30;

        g.drawString("Same Game", textX, textY);
        g.drawString(message, textX, textY + 30);
        g.drawString(message2, textX, textY + 60);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Controls:", textX, textY + 120);
        g.drawString("Arrow Keys: Move selection", textX, textY + 140);
        g.drawString("P: Pop connected blocks", textX, textY + 160);
        g.drawString("M: Return to Menu", textX, textY + 180);
    }

    private void loadGridSave() {
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                Block block = gridSave.getBlock(row, col);
                if (block != null) {
                    grid.placeBlock(new Block(block.getRow(), block.getColumn(), block.getType(), block.getColor()),
                            row, col);
                }
            }
        }
    }

    private void checkSwitchPlayer() {
        if (grid.isGridEmpty() && !playerOneFinished) {
            switchPlayers();
            playerOneFinished = true;
            message = getCurrPlayerName() + "'s Turn!";
            message2 = "Turns: 0";
            loadGridSave();
        }
    }

    @Override
    protected boolean checkGameOver() {
        if (grid.isGridEmpty() && playerOneFinished) {
            if (players.get(0).getScore() > players.get(1).getScore()) {
                message = players.get(1).getName() + " Wins!";
                player2Wins++;
            } else if (players.get(0).getScore() < players.get(1).getScore()) {
                message = players.get(0).getName() + " Wins!";
                player1Wins++;
            } else {
                message = "It's a Tie!";
            }
            message2 = players.get(0).getName() + " Turns = " + players.get(0).getScore() + " | " +
                    players.get(1).getName() + " Turns = " + players.get(1).getScore();
            message = message + " | Wins - " + players.get(0).getName() + ": " + player1Wins + " | " +
                    players.get(1).getName() + ": " + player2Wins;
            playerOneFinished = false;
            gridSave.clear();
            switchPlayers();
            resetAllPlayers();
            return true;
        }
        return false;
    }
}