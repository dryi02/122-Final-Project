package tilematch;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;

/**
 * A demonstration class that shows a grid with blocks that can be interacted
 * with.
 * This is a simplified version without game mechanics, just to test display and
 * interaction.
 */
public class BejeweledGameState extends GameState {
    private static final Random RANDOM = new Random();
    private static final Color[] BLOCK_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
    };

    // Minimum number of connected blocks required for popping
    private static final int MIN_BLOCKS_TO_POP = 3;

    // Maximum attempts to generate a grid without matches
    private static final int MAX_GENERATION_ATTEMPTS = 100;

    private static final int PLAYER_TIME_LIMIT = 30; // 30 seconds per player
    private static final int GLOBAL_TIME_LIMIT = 300; // 5 minutes in seconds
    private int currentPlayerTime = PLAYER_TIME_LIMIT;
    private int globalTime = GLOBAL_TIME_LIMIT;
    private long lastTimeUpdate = System.currentTimeMillis();
    private boolean isTimerRunning = true;
    private boolean isGameOver = false;
    private int player1Wins = 0;
    private int player2Wins = 0;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private int swapRow = -1;
    private int swapCol = -1;
    private boolean swapMode = false;
    private String message = getCurrPlayerName() + "'s Turn!";
    private String message2 = "Click arrow keys to move selection";
    private String message3 = "Score: " + getCurrPlayerScore();
    private boolean playerOneFinished = false;

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
     * Creates a new BejeweledGameState with the specified dimensions.
     *
     * @param rows    The number of rows in the grid
     * @param columns The number of columns in the grid
     */
    public BejeweledGameState(int rows, int columns) {
        super(rows, columns);
        // Load win counts from GameChooser
        int[] wins = GameChooser.getPlayerWins();
        player1Wins = wins[0];
        player2Wins = wins[1];
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

    /**
     * Initializes the grid with random blocks, ensuring no initial matches.
     */
    private void initializeGridWithoutMatches() {
        // Try to generate a grid without matches
        int attempts = 0;
        boolean validGrid = false;

        while (!validGrid && attempts < MAX_GENERATION_ATTEMPTS) {
            // Clear the grid
            grid.clear();

            // Fill the grid with random blocks
            for (int row = 0; row < grid.getRows(); row++) {
                for (int col = 0; col < grid.getColumns(); col++) {
                    // Get a list of colors that would not create a match
                    List<Color> validColors = getValidColorsForPosition(row, col);

                    if (validColors.isEmpty()) {
                        // If no valid colors, use any color (this should be rare)
                        Color color = BLOCK_COLORS[RANDOM.nextInt(BLOCK_COLORS.length)];
                        Block block = new Block(row, col, Block.BlockType.STANDARD, color);
                        grid.placeBlock(block, row, col);
                    } else {
                        // Choose a random color from valid colors
                        Color color = validColors.get(RANDOM.nextInt(validColors.size()));
                        Block block = new Block(row, col, Block.BlockType.STANDARD, color);
                        grid.placeBlock(block, row, col);
                    }
                }
            }

            // Check if the grid has any matches
            validGrid = !hasMatches();
            attempts++;
        }

        if (!validGrid) {
            // If we couldn't generate a valid grid, just use a simple pattern
            createPatternedGrid();
        }
    }

    /**
     * Creates a grid with a simple pattern that guarantees no matches.
     */
    private void createPatternedGrid() {
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                // Use a checkerboard-like pattern with alternating colors
                int colorIndex = (row + col) % BLOCK_COLORS.length;
                Color color = BLOCK_COLORS[colorIndex];
                Block block = new Block(row, col, Block.BlockType.STANDARD, color);
                grid.placeBlock(block, row, col);
            }
        }
    }

    /**
     * Gets a list of colors that would not create a match at the specified
     * position.
     * 
     * @param row The row position
     * @param col The column position
     * @return A list of valid colors
     */
    private List<Color> getValidColorsForPosition(int row, int col) {
        List<Color> validColors = new ArrayList<>();

        for (Color color : BLOCK_COLORS) {
            if (isValidColorAtPosition(row, col, color)) {
                validColors.add(color);
            }
        }

        return validColors;
    }

    /**
     * Checks if placing a block with the specified color at the position would
     * create a match.
     * 
     * @param row   The row position
     * @param col   The column position
     * @param color The color to check
     * @return True if the color is valid (doesn't create a match), false otherwise
     */
    private boolean isValidColorAtPosition(int row, int col, Color color) {
        // Check horizontal matches (need at least 2 same-colored blocks to the left)
        if (col >= 2 &&
                grid.isOccupied(row, col - 1) &&
                grid.isOccupied(row, col - 2) &&
                grid.getBlock(row, col - 1).getColor().equals(color) &&
                grid.getBlock(row, col - 2).getColor().equals(color)) {
            return false;
        }

        // Check vertical matches (need at least 2 same-colored blocks above)
        if (row >= 2 &&
                grid.isOccupied(row - 1, col) &&
                grid.isOccupied(row - 2, col) &&
                grid.getBlock(row - 1, col).getColor().equals(color) &&
                grid.getBlock(row - 2, col).getColor().equals(color)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the grid has any matches.
     * 
     * @return True if matches exist, false otherwise
     */
    private boolean hasMatches() {
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                if (grid.isOccupied(row, col)) {
                    Block block = grid.getBlock(row, col);
                    Set<Point> matches = findConnectedBlocks(row, col, block.getColor());

                    if (matches.size() >= MIN_BLOCKS_TO_POP) {
                        return true;
                    }
                }
            }
        }

        return false;
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
            case "R":
                randomizeGrid();
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

    /**
     * Toggles swap mode on/off.
     */
    private void toggleSwapMode() {
        swapMode = !swapMode;
        if (swapMode) {
            swapRow = -1;
            swapCol = -1;
        }
    }

    /**
     * Swaps two blocks and checks for matches.
     */
    private void swapBlocks() {
        if (!grid.isOccupied(selectedRow, selectedCol)) {
            message2 = "No block to swap at current position.";
            return;
        }

        if (swapRow == -1 || swapCol == -1) {
            // First block selected
            swapRow = selectedRow;
            swapCol = selectedCol;
            message2 = "First block selected at (" + swapRow + ", " + swapCol + "). Select second block.";
        } else {
            // Second block selected, check if it's adjacent
            boolean isAdjacent = (Math.abs(selectedRow - swapRow) == 1 && selectedCol == swapCol) ||
                    (Math.abs(selectedCol - swapCol) == 1 && selectedRow == swapRow);

            if (!isAdjacent) {
                message2 = "Blocks must be adjacent to swap.";
                return;
            }

            if (!grid.isOccupied(selectedRow, selectedCol)) {
                message2 = "No block to swap at second position.";
                return;
            }

            // Perform the swap
            Block block1 = grid.removeBlock(swapRow, swapCol);
            Block block2 = grid.removeBlock(selectedRow, selectedCol);

            // Update block positions
            block1.setPosition(selectedRow, selectedCol);
            block2.setPosition(swapRow, swapCol);

            // Place blocks in swapped positions
            grid.placeBlock(block1, selectedRow, selectedCol);
            grid.placeBlock(block2, swapRow, swapCol);

            // Check for matches after swap
            boolean matchFound = checkForMatches();

            if (!matchFound) {
                // Swap back if no matches
                block1 = grid.removeBlock(selectedRow, selectedCol);
                block2 = grid.removeBlock(swapRow, swapCol);

                block1.setPosition(swapRow, swapCol);
                block2.setPosition(selectedRow, selectedCol);

                grid.placeBlock(block1, swapRow, swapCol);
                grid.placeBlock(block2, selectedRow, selectedCol);

                message2 = "No matches found. Swap reversed.";
            } else {
                message2 = "Blocks swapped and matches found!";
                // Switch players and reset timer after successful move
                switchPlayers();
                currentPlayerTime = PLAYER_TIME_LIMIT;
                lastTimeUpdate = System.currentTimeMillis();
                message = getCurrPlayerName() + "'s Turn!";
                message3 = "Score: " + getCurrPlayerScore();
            }

            // Reset swap selection
            swapRow = -1;
            swapCol = -1;
        }
    }

    /**
     * Checks for matches across the entire board.
     * 
     * @return true if any matches were found and processed
     */
    private boolean checkForMatches() {
        Set<Point> allMatches = new HashSet<>();

        // Check each cell for potential matches
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                if (grid.isOccupied(row, col)) {
                    Block block = grid.getBlock(row, col);
                    Set<Point> matches = findConnectedBlocks(row, col, block.getColor());

                    if (matches.size() >= MIN_BLOCKS_TO_POP) {
                        allMatches.addAll(matches);
                    }
                }
            }
        }

        if (!allMatches.isEmpty()) {
            // Remove all matched blocks
            for (Point p : allMatches) {
                grid.removeBlock(p.x, p.y);
            }

            addCurrPlayerScore(allMatches.size() * 10);
            message2 = "Popped " + allMatches.size() + " blocks!";

            // Apply gravity and fill empty spaces
            applyGravity();
            fillEmptySpaces();

            // Check for new matches after blocks fall and new blocks are added
            if (checkForMatches()) {
                // If new matches were found, don't switch players yet
                return true;
            }

            // Only switch players if no more matches are found
            switchPlayers();
            currentPlayerTime = PLAYER_TIME_LIMIT;
            lastTimeUpdate = System.currentTimeMillis();
            message = getCurrPlayerName() + "'s Turn!";
            message3 = "Score: " + getCurrPlayerScore();
            return true;
        }
        return false;
    }

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
     * Fills empty spaces at the top of the grid with new random blocks.
     */
    private void fillEmptySpaces() {
        for (int col = 0; col < grid.getColumns(); col++) {
            for (int row = 0; row < grid.getRows(); row++) {
                if (!grid.isOccupied(row, col)) {
                    Color color = BLOCK_COLORS[RANDOM.nextInt(BLOCK_COLORS.length)];
                    Block block = new Block(row, col, Block.BlockType.STANDARD, color);
                    grid.placeBlock(block, row, col);
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
        resetAllPlayers();
        currentPlayerTime = PLAYER_TIME_LIMIT;
        globalTime = GLOBAL_TIME_LIMIT;
        isTimerRunning = true;
        isGameOver = false;
        message = getCurrPlayerName() + "'s Turn!";
        message3 = "Score: " + getCurrPlayerScore() + " | Wins - " +
                players.get(0).getName() + ": " + player1Wins + " | " +
                players.get(1).getName() + ": " + player2Wins;
    }

    /**
     * Clears all blocks from the grid.
     */
    private void clearGrid() {
        grid.clear();
        resetActivePlayer();
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
                    message3 = "Press R to start a new game";
                    isTimerRunning = false;
                    return;
                }

                // Check player timer
                if (currentPlayerTime <= 0) {
                    // Time's up for current player
                    isTimerRunning = false;
                    message2 = "Time's up! Switching players...";
                    switchPlayers();
                    currentPlayerTime = PLAYER_TIME_LIMIT;
                    isTimerRunning = true;
                    message = getCurrPlayerName() + "'s Turn!";
                    message3 = "Score: " + getCurrPlayerScore();
                }
            }
        }
    }

    @Override
    protected void renderUI(Graphics g) {
        // Draw instructions
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        int textX = grid.getXOffset() + grid.getColumns() * grid.getCellSize() + 20;
        int textY = grid.getYOffset() + 30;

        g.drawString("Bejeweled", textX, textY);
        g.drawString(message, textX, textY + 30);
        g.drawString(message2, textX, textY + 60);
        g.drawString(message3, textX, textY + 90);
        g.drawString("Player Time: " + currentPlayerTime + "s", textX, textY + 120);
        g.drawString("Game Time: " + (globalTime / 60) + ":" + String.format("%02d", globalTime % 60), textX,
                textY + 150);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Controls:", textX, textY + 180);
        g.drawString("Arrow Keys: Move selection", textX, textY + 200);
        g.drawString("Space: Toggle block/Confirm swap", textX, textY + 220);
        g.drawString("R: New Game", textX, textY + 240);
        g.drawString("M: Return to Menu", textX, textY + 260);

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
    // protected void checkSwitchPlayer() {
    // if(hasMatches() && this.playerOneFinished == false) {
    // switchPlayers();
    // this.playerOneFinished = true;
    // message = getCurrPlayerName() + "'s Turn!";
    // message3 = "Score: " + getCurrPlayerScore();
    // }
    // }

    @Override
    protected void checkGameOver() {
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
            message3 = "Wins - " + players.get(0).getName() + ": " + player1Wins + " | " +
                    players.get(1).getName() + ": " + player2Wins + " | Press R to start a new game";

            // Reset game state after displaying final scores
            playerOneFinished = false;
            isTimerRunning = false;
            clearGrid();
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