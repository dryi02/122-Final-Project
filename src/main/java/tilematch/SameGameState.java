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
    private static final Random RANDOM = new Random();
    private static final Color[] BLOCK_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
    };

    private int selectedRow = -1;
    private int selectedCol = -1;
    private int swapRow = -1;
    private int swapCol = -1;
    private boolean swapMode = false;
    private String message = getCurrPlayerName() + "'s Turn!";
    private String message2 = "Turns: " + getCurrPlayerScore();
    private Grid gridSave;
    private boolean playerOneFinished = false;
    private int player1Wins = 0;
    private int player2Wins = 0;

    /**
     * Sets custom names for both players.
     * 
     * @param player1Name Name for Player 1
     * @param player2Name Name for Player 2
     */
    public void setPlayerNames(String player1Name, String player2Name) {
        players.get(0).setName(player1Name);
        players.get(1).setName(player2Name);
        message = getCurrPlayerName() + "'s Turn!";
    }

    /**
     * Creates a new GridDemoState with the specified dimensions.
     *
     * @param rows    The number of rows in the grid
     * @param columns The number of columns in the grid
     */
    public SameGameState(int rows, int columns) {
        super(rows, columns);
        // Load win counts from GameChooser
        int[] wins = GameChooser.getPlayerWins();
        player1Wins = wins[0];
        player2Wins = wins[1];
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
            case "R":
                randomizeGrid();
                break;
            case "C":
                clearGrid();
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
        Set<Point> connectedBlocks = findConnectedBlocks(selectedRow, selectedCol, targetColor);

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
            applyGravity();
            checkSwitchPlayer();

            // Fill empty spaces at the top with new blocks
            // fillEmptySpaces();

            // Check for cascading matches
            // checkCascadingMatches();
        } else {
            message = "Need at least " + 1 + " connected blocks to pop";
        }
    }

    /**
     * Finds all connected blocks of the same color using Breadth-First Search.
     * 
     * @param startRow    The starting row
     * @param startCol    The starting column
     * @param targetColor The color to match
     * @return A set of points representing connected blocks
     */
    private Set<Point> findConnectedBlocks(int startRow, int startCol, Color targetColor) {
        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();

        // Add the starting point
        Point start = new Point(startRow, startCol);
        queue.add(start);
        visited.add(start);

        // Define the four directions: up, right, down, left
        int[][] directions = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };

        // BFS to find all connected blocks of the same color
        while (!queue.isEmpty()) {
            Point current = queue.poll();

            // Check all four adjacent positions
            for (int[] dir : directions) {
                int newRow = current.x + dir[0];
                int newCol = current.y + dir[1];
                Point newPoint = new Point(newRow, newCol);

                // Check if the position is valid and not visited
                if (grid.isValidPosition(newRow, newCol) &&
                        grid.isOccupied(newRow, newCol) &&
                        !visited.contains(newPoint)) {

                    Block block = grid.getBlock(newRow, newCol);

                    // If the block has the same color, add it to the queue
                    if (block.getColor().equals(targetColor)) {
                        queue.add(newPoint);
                        visited.add(newPoint);
                    }
                }
            }
        }

        return visited;
    }

    /**
     * Applies gravity to make blocks fall after popping.
     */
    private void applyGravity() {
        // For each column
        for (int col = 0; col < grid.getColumns(); col++) {
            // Start from the bottom row
            for (int row = grid.getRows() - 1; row >= 0; row--) {
                // If the cell is empty
                if (!grid.isOccupied(row, col)) {
                    // Find the first non-empty cell above
                    int aboveRow = row - 1;
                    while (aboveRow >= 0) {
                        if (grid.isOccupied(aboveRow, col)) {
                            // Move the block down
                            Block block = grid.removeBlock(aboveRow, col);
                            grid.placeBlock(block, row, col);
                            break;
                        }
                        aboveRow--;
                    }
                }
            }
        }
    }

    /**
     * Randomizes the grid with new blocks.
     */
    private void randomizeGrid() {
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
    private void clearGrid() {
        grid.clear();
        resetActivePlayer();
        message = "Grid cleared";
    }

    @Override
    protected void updateGame(double deltaTime) {
        // No game logic to update in this demo
    }

    @Override
    protected void renderUI(Graphics g) {
        // Draw instructions
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
        g.drawString("R: New Game", textX, textY + 180);
        g.drawString("C: Clear grid", textX, textY + 200);
        g.drawString("M: Return to Menu", textX, textY + 220);

        // Draw selection highlight
        if (selectedRow >= 0 && selectedCol >= 0) {
            g.setColor(new Color(255, 255, 255, 100)); // Semi-transparent white
            int x = grid.getXOffset() + selectedCol * grid.getCellSize();
            int y = grid.getYOffset() + selectedRow * grid.getCellSize();
            g.fillRect(x, y, grid.getCellSize(), grid.getCellSize());

            g.setColor(Color.WHITE);
            g.drawRect(x, y, grid.getCellSize(), grid.getCellSize());
        }

        // Draw swap selection highlight if in swap mode and first block is selected
        if (swapMode && swapRow >= 0 && swapCol >= 0) {
            g.setColor(new Color(255, 255, 0, 100)); // Semi-transparent yellow
            int x = grid.getXOffset() + swapCol * grid.getCellSize();
            int y = grid.getYOffset() + swapRow * grid.getCellSize();
            g.fillRect(x, y, grid.getCellSize(), grid.getCellSize());

            g.setColor(Color.YELLOW);
            g.drawRect(x, y, grid.getCellSize(), grid.getCellSize());
        }
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
    protected void checkGameOver() {
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
        }
    }

    @Override
    public void render(Graphics g) {
        // Render the grid (which includes the blocks)
        grid.render(g);

        // Render UI elements
        renderUI(g);
    }
}