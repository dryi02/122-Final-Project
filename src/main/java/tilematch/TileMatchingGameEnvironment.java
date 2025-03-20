package tilematch;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The TileMatchingGameEnvironment class provides common functionality for tile
 * matching games.
 * It extends GameState and implements core mechanics like block swapping,
 * matching, and gravity.
 */
public abstract class TileMatchingGameEnvironment extends GameState {
    // Minimum number of connected blocks required for popping
    protected static final int MIN_BLOCKS_TO_POP = 3;

    // Maximum attempts to generate a grid without matches
    protected static final int MAX_GENERATION_ATTEMPTS = 100;

    protected boolean swapMode = false;
    protected int swapRow = -1;
    protected int swapCol = -1;
    protected String message2 = "Click arrow keys to move selection";

    public TileMatchingGameEnvironment(int rows, int columns) {
        super(rows, columns);
    }

    /**
     * Gets a list of colors that would not create a match at the specified
     * position.
     */
    public List<Color> getValidColorsForPosition(int row, int col) {
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
     */
    public boolean isValidColorAtPosition(int row, int col, Color color) {
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
     * Creates a grid with a simple pattern that guarantees no matches.
     */
    public void createPatternedGrid() {
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
     * Checks if the grid has any matches.
     */
    public boolean hasMatches() {
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                if (grid.isOccupied(row, col)) {
                    Block block = grid.getBlock(row, col);
                    Set<Point> matches = grid.findConnectedBlocks(row, col, block.getColor());

                    if (matches.size() >= MIN_BLOCKS_TO_POP) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Toggles swap mode on/off.
     */
    public void toggleSwapMode() {
        swapMode = !swapMode;
        if (swapMode) {
            swapRow = -1;
            swapCol = -1;
        }
    }

    /**
     * Swaps two blocks and checks for matches.
     */
    public void swapBlocks() {
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
            }

            // Reset swap selection
            swapRow = -1;
            swapCol = -1;
        }
    }

    /**
     * Checks for matches across the entire board.
     */
    public boolean checkForMatches() {
        Set<Point> allMatches = new HashSet<>();

        // Check each cell for potential matches
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                if (grid.isOccupied(row, col)) {
                    Block block = grid.getBlock(row, col);
                    Set<Point> matches = grid.findConnectedBlocks(row, col, block.getColor());

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
            grid.applyGravity();
            fillEmptySpaces();

            // Check for new matches after blocks fall and new blocks are added
            if (checkForMatches()) {
                // If new matches were found, don't switch players yet
                return true;
            }

            // Only switch players if no more matches are found
            switchPlayers();
            message = getCurrPlayerName() + "'s Turn!";
            return true;
        }
        return false;
    }

    /**
     * Fills empty spaces at the top of the grid with new random blocks.
     */
    public void fillEmptySpaces() {
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
     * Initializes the grid with random blocks, ensuring no initial matches.
     */
    public void initializeGridWithoutMatches() {
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
     * Initializes the grid with random blocks, encouraging matches.
     */
    protected void initializeGridWithMatches() {
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
            }
        }
    }

    /**
     * Pops (removes) all connected blocks of the same color at the selected
     * position.
     */
    protected void popConnectedBlocks() {
        if (!grid.isOccupied(selectedRow, selectedCol)) {
            message2 = "No block to pop at (" + selectedRow + ", " + selectedCol + ")";
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
            addCurrPlayerScore(connectedBlocks.size());
            message2 = "Popped " + connectedBlocks.size() + " blocks!";

            // Apply gravity to make blocks fall
            grid.applyGravity();
        } else {
            message2 = "Need at least " + MIN_BLOCKS_TO_POP + " connected blocks to pop";
        }
    }

    @Override
    protected void renderSelectionHighlight(Graphics g) {
        if (selectedRow >= 0 && selectedCol >= 0) {
            g.setColor(new Color(255, 255, 255, 100)); // Semi-transparent white
            int x = grid.getXOffset() + selectedCol * grid.getCellSize();
            int y = grid.getYOffset() + selectedRow * grid.getCellSize();
            g.fillRect(x, y, grid.getCellSize(), grid.getCellSize());

            g.setColor(Color.WHITE);
            g.drawRect(x, y, grid.getCellSize(), grid.getCellSize());
        }
    }

    @Override
    protected void renderSwapSelectionHighlight(Graphics g) {
        if (swapMode && swapRow >= 0 && swapCol >= 0) {
            g.setColor(new Color(255, 255, 0, 100)); // Semi-transparent yellow
            int x = grid.getXOffset() + swapCol * grid.getCellSize();
            int y = grid.getYOffset() + swapRow * grid.getCellSize();
            g.fillRect(x, y, grid.getCellSize(), grid.getCellSize());

            g.setColor(Color.YELLOW);
            g.drawRect(x, y, grid.getCellSize(), grid.getCellSize());
        }
    }
}