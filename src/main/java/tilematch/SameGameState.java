package tilematch;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 * A demonstration class that shows a grid with blocks that can be interacted
 * with.
 * This is a simplified version without game mechanics, just to test display and
 * interaction.
 */
public class SameGameState extends GameState {
    private String message2 = "Turns: " + getCurrPlayerScore();
    private Grid gridSave;

    /**
     * Sets custom names for both players.
     * 
     * @param player1Name Name for Player 1
     * @param player2Name Name for Player 2
     */
    // public void setPlayerNames(String player1Name, String player2Name) {
    // players.get(0).setName(player1Name);
    // players.get(1).setName(player2Name);
    // message = getCurrPlayerName() + "'s Turn!";
    // }

    /**
     * Creates a new GridDemoState with the specified dimensions.
     *
     * @param rows    The number of rows in the grid
     * @param columns The number of columns in the grid
     */
    public SameGameState(int rows, int columns) {
        super(rows, columns);
        // Load win counts from GameChooser
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
    }

    /**
     * Initializes the grid with random blocks, allowing matches.
     */
    private void initializeGridWithMatches() {
        // Create a board that encourages matches by making neighboring blocks likely to
        // have the same color
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                Color color;
                if (row > 0 && RANDOM.nextDouble() < 0.7) {
                    // 70% chance to inherit the color from the block above
                    color = grid.getBlock(row - 1, col).getColor();
                } else if (col > 0 && RANDOM.nextDouble() < 0.7) {
                    // 70% chance to inherit the color from the block to the left
                    color = grid.getBlock(row, col - 1).getColor();
                } else {
                    // Otherwise, pick a random color
                    color = BLOCK_COLORS[RANDOM.nextInt(BLOCK_COLORS.length)];
                }
                Block block = new Block(row, col, Block.BlockType.STANDARD, color);
                grid.placeBlock(block, row, col);
                gridSave.placeBlock(block, row, col);
            }
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
     * Pops (removes) all connected blocks of the same color at the selected
     * position.
     */
    private void popConnectedBlocks() {
        if (!grid.isOccupied(selectedRow, selectedCol)) {
            message = "No block to pop at (" + selectedRow + ", " + selectedCol + ")";
            return;
        }

        Block selectedBlock = grid.getBlock(selectedRow, selectedCol);
        Color targetColor = selectedBlock.getColor();

        // Find all connected blocks of the same color using BFS
        Set<Point> connectedBlocks = grid.findConnectedBlocks(selectedRow, selectedCol, targetColor);

        // Only pop if there are at least MIN_BLOCKS_TO_POP connected blocks
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
            checkSwitchPlayer();
        } else {
            message = "Need at least " + 1 + " connected blocks to pop";
        }
    }

    /**
     * Randomizes the grid with new blocks.
     */
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
        // No game logic to update in this demo
    }

    // protected void renderUI(Graphics g) {
    // renderInstructions(g);
    // renderSelectionHighlight(g);
    // renderSwapSelectionHighlight(g);
    // }

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

    protected void loadGridSave() {
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                grid.placeBlock(gridSave.getBlock(row, col), row, col);
            }
        }
    }

    protected void checkSwitchPlayer() {
        if (grid.isGridEmpty() && this.playerOneFinished == false) {
            switchPlayers();
            this.playerOneFinished = true;
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

    // @Override
    // public void render(Graphics g) {
    // // Render the grid (which includes the blocks)
    // grid.render(g);
    //
    // // Render UI elements
    // renderUI(g);
    // }
}