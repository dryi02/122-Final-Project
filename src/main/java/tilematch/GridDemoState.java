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

/**
 * A demonstration class that shows a grid with blocks that can be interacted
 * with.
 * This is a simplified version without game mechanics, just to test display and
 * interaction.
 */
public class GridDemoState extends GameState {
    private static final Random RANDOM = new Random();
    private static final Color[] BLOCK_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
    };

    // Minimum number of connected blocks required for popping
    private static final int MIN_BLOCKS_TO_POP = 3;

    // Maximum attempts to generate a grid without matches
    private static final int MAX_GENERATION_ATTEMPTS = 100;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private int swapRow = -1;
    private int swapCol = -1;
    private boolean swapMode = false;
    private String message = "Click arrow keys to move selection";
    private int score = 0;
    private boolean allowInitialMatches = true;

    /**
     * Creates a new GridDemoState with the specified dimensions.
     *
     * @param rows    The number of rows in the grid
     * @param columns The number of columns in the grid
     */
    public GridDemoState(int rows, int columns) {
        super(rows, columns);
        initializeGrid();
    }

    /**
     * Initializes the grid with random blocks.
     */
    private void initializeGrid() {
        if (allowInitialMatches) {
            initializeGridWithMatches();
        } else {
            initializeGridWithoutMatches();
        }

        // Select the center block initially
        selectedRow = grid.getRows() / 2;
        selectedCol = grid.getColumns() / 2;
    }

    /**
     * Initializes the grid with random blocks, allowing matches.
     */
    private void initializeGridWithMatches() {
        // Create a board that encourages matches by making neighboring blocks likely to have the same color
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
            }
        }
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

        message = "Grid initialized without matches";
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
            case "SPACE":
                if (swapMode) {
                    swapBlocks();
                } else {
                    toggleBlock();
                }
                break;
            case "S":
                toggleSwapMode();
                break;
            case "P":
                popConnectedBlocks();
                break;
            case "R":
                randomizeGrid();
                break;
            case "M":
                toggleAllowInitialMatches();
                break;
            case "C":
                clearGrid();
                break;
            case "A":
                checkAllMatches();
                break;
        }
    }

    /**
     * Toggles whether initial matches are allowed when randomizing the grid.
     */
    private void toggleAllowInitialMatches() {
        allowInitialMatches = !allowInitialMatches;
        message = "Initial matches " + (allowInitialMatches ? "allowed" : "not allowed") + " when randomizing";
    }

    /**
     * Toggles swap mode on/off.
     */
    private void toggleSwapMode() {
        swapMode = !swapMode;
        if (swapMode) {
            swapRow = -1;
            swapCol = -1;
            message = "Swap mode ON. Select first block.";
        } else {
            message = "Swap mode OFF.";
        }
    }

    /**
     * Swaps two blocks and checks for matches.
     */
    private void swapBlocks() {
        if (!grid.isOccupied(selectedRow, selectedCol)) {
            message = "No block to swap at current position.";
            return;
        }

        if (swapRow == -1 || swapCol == -1) {
            // First block selected
            swapRow = selectedRow;
            swapCol = selectedCol;
            message = "First block selected at (" + swapRow + ", " + swapCol + "). Select second block.";
        } else {
            // Second block selected, check if it's adjacent
            boolean isAdjacent = (Math.abs(selectedRow - swapRow) == 1 && selectedCol == swapCol) ||
                    (Math.abs(selectedCol - swapCol) == 1 && selectedRow == swapRow);

            if (!isAdjacent) {
                message = "Blocks must be adjacent to swap.";
                return;
            }

            if (!grid.isOccupied(selectedRow, selectedCol)) {
                message = "No block to swap at second position.";
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

                message = "No matches found. Swap reversed.";
            } else {
                message = "Blocks swapped and matches found!";
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

            // Update score
            score += allMatches.size() * 10;
            message = "Popped " + allMatches.size() + " blocks! Score: " + score;

            // Apply gravity and fill empty spaces
            applyGravity();
            fillEmptySpaces();

            // Check for cascading matches
            checkCascadingMatches();

            return true;
        }

        return false;
    }

    /**
     * Checks for cascading matches after blocks have fallen.
     */
    private void checkCascadingMatches() {
        // Use a small delay to allow for animation if needed
        boolean moreMatches = checkForMatches();

        if (moreMatches) {
            message += " Cascade bonus!";
        }
    }

    /**
     * Manually checks the entire board for matches.
     */
    private void checkAllMatches() {
        boolean matchesFound = checkForMatches();

        if (!matchesFound) {
            message = "No matches found on the board.";
        }
    }

    /**
     * Toggles a block at the selected position.
     * If there's a block, it will be removed. If there's no block, a new one will
     * be added.
     */
    private void toggleBlock() {
        if (grid.isOccupied(selectedRow, selectedCol)) {
            grid.removeBlock(selectedRow, selectedCol);
            message = "Removed block at (" + selectedRow + ", " + selectedCol + ")";
        } else {
            Color color = BLOCK_COLORS[RANDOM.nextInt(BLOCK_COLORS.length)];
            Block block = new Block(selectedRow, selectedCol, Block.BlockType.STANDARD, color);
            grid.placeBlock(block, selectedRow, selectedCol);
            message = "Added block at (" + selectedRow + ", " + selectedCol + ")";
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
        if (connectedBlocks.size() >= MIN_BLOCKS_TO_POP) {
            // Remove all connected blocks
            for (Point p : connectedBlocks) {
                grid.removeBlock(p.x, p.y);
            }

            // Update score and message
            score += connectedBlocks.size() * 10;
            message = "Popped " + connectedBlocks.size() + " blocks! Score: " + score;

            // Apply gravity to make blocks fall
            applyGravity();

            // Fill empty spaces at the top with new blocks
            //fillEmptySpaces();

            // Check for cascading matches
            //checkCascadingMatches();
        } else {
            message = "Need at least " + MIN_BLOCKS_TO_POP + " connected blocks to pop";
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
    // private Set<Point> findConnectedBlocks(int startRow, int startCol, Color targetColor) {
    //     Set<Point> visited = new HashSet<>();
    //     Queue<Point> queue = new LinkedList<>();

    //     // Add the starting point
    //     Point start = new Point(startRow, startCol);
    //     queue.add(start);
    //     visited.add(start);

    //     // Define the four directions: up, right, down, left
    //     int[][] directions = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };

    //     // BFS to find all connected blocks of the same color
    //     while (!queue.isEmpty()) {
    //         Point current = queue.poll();

    //         // Check all four adjacent positions
    //         for (int[] dir : directions) {
    //             int newRow = current.x + dir[0];
    //             int newCol = current.y + dir[1];
    //             Point newPoint = new Point(newRow, newCol);

    //             // Check if the position is valid and not visited
    //             if (grid.isValidPosition(newRow, newCol) &&
    //                     grid.isOccupied(newRow, newCol) &&
    //                     !visited.contains(newPoint)) {

    //                 Block block = grid.getBlock(newRow, newCol);

    //                 // If the block has the same color, add it to the queue
    //                 if (block.getColor().equals(targetColor)) {
    //                     queue.add(newPoint);
    //                     visited.add(newPoint);
    //                 }
    //             }
    //         }
    //     }

    //     return visited;
    // }

    // /**
    //  * Applies gravity to make blocks fall after popping.
    //  */
    // private void applyGravity() {
    //     // For each column
    //     for (int col = 0; col < grid.getColumns(); col++) {
    //         // Start from the bottom row
    //         for (int row = grid.getRows() - 1; row >= 0; row--) {
    //             // If the cell is empty
    //             if (!grid.isOccupied(row, col)) {
    //                 // Find the first non-empty cell above
    //                 int aboveRow = row - 1;
    //                 while (aboveRow >= 0) {
    //                     if (grid.isOccupied(aboveRow, col)) {
    //                         // Move the block down
    //                         Block block = grid.removeBlock(aboveRow, col);
    //                         grid.placeBlock(block, row, col);
    //                         break;
    //                     }
    //                     aboveRow--;
    //                 }
    //             }
    //         }
    //     }
    // }

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
    public void randomizeGrid() {
        grid.clear();
        initializeGrid();
        score = 0;
        message = "Grid randomized" + (allowInitialMatches ? " with" : " without") + " initial matches";
    }

    /**
     * Clears all blocks from the grid.
     */
    public void clearGrid() {
        grid.clear();
        score = 0;
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

        g.drawString("Grid Demo", textX, textY);
        g.drawString(message, textX, textY + 30);
        g.drawString("Score: " + score, textX, textY + 60);
        g.drawString("Initial Matches: " + (allowInitialMatches ? "Allowed" : "Not Allowed"), textX, textY + 80);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Controls:", textX, textY + 120);
        g.drawString("Arrow Keys: Move selection", textX, textY + 140);
        g.drawString("Space: Toggle block/Confirm swap", textX, textY + 160);
        g.drawString("S: Toggle swap mode", textX, textY + 180);
        g.drawString("P: Pop connected blocks", textX, textY + 200);
        g.drawString("A: Check all matches", textX, textY + 220);
        g.drawString("M: Toggle initial matches", textX, textY + 240);
        g.drawString("R: Randomize grid", textX, textY + 260);
        g.drawString("C: Clear grid", textX, textY + 280);

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

    @Override
    protected boolean checkGameOver() {
        // No game over condition in this demo
    	return false;
    }

    @Override
    public void render(Graphics g) {
        // Render the grid (which includes the blocks)
        grid.render(g);

        // Render UI elements
        renderUI(g);
    }

	@Override
	protected void renderInstructions(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}