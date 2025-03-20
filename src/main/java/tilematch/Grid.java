package tilematch;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * The Grid class represents the game grid.
 * It manages the placement and removal of blocks on the grid.
 */
public class Grid {
    private Block[][] cells;
    private int rows;
    private int columns;
    private int cellSize;
    private int xOffset;
    private int yOffset;

    /**
     * Creates a new Grid with the specified dimensions.
     *
     * @param rows    The number of rows in the grid
     * @param columns The number of columns in the grid
     */
    public Grid(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.cells = new Block[rows][columns];
        this.cellSize = 30; // Default cell size
        this.xOffset = 50; // Default X offset
        this.yOffset = 50; // Default Y offset
    }

    /**
     * Creates a new Grid with the specified dimensions and cell size.
     *
     * @param rows     The number of rows in the grid
     * @param columns  The number of columns in the grid
     * @param cellSize The size of each cell in pixels
     * @param xOffset  The X offset of the grid in pixels
     * @param yOffset  The Y offset of the grid in pixels
     */
    public Grid(int rows, int columns, int cellSize, int xOffset, int yOffset) {
        this.rows = rows;
        this.columns = columns;
        this.cells = new Block[rows][columns];
        this.cellSize = cellSize;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    /**
     * Places a block on the grid at the specified position.
     *
     * @param block  The block to place
     * @param row    The row to place the block at
     * @param column The column to place the block at
     * @return True if the block was placed successfully, false otherwise
     */
    public boolean placeBlock(Block block, int row, int column) {
        if (isValidPosition(row, column) && cells[row][column] == null) {
            cells[row][column] = block;
            block.setPosition(row, column);
            return true;
        }
        return false;
    }

    /**
     * Removes a block from the grid at the specified position.
     *
     * @param row    The row to remove the block from
     * @param column The column to remove the block from
     * @return The removed block, or null if there was no block at the position
     */
    public Block removeBlock(int row, int column) {
        if (isValidPosition(row, column) && cells[row][column] != null) {
            Block block = cells[row][column];
            cells[row][column] = null;
            return block;
        }
        return null;
    }

    /**
     * Checks if a position on the grid is valid.
     *
     * @param row    The row to check
     * @param column The column to check
     * @return True if the position is valid, false otherwise
     */
    public boolean isValidPosition(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    /**
     * Checks if a position on the grid is occupied.
     *
     * @param row    The row to check
     * @param column The column to check
     * @return True if the position is occupied, false otherwise
     */
    public boolean isOccupied(int row, int column) {
        return isValidPosition(row, column) && cells[row][column] != null;
    }

    /**
     * Gets the block at the specified position.
     *
     * @param row    The row to get the block from
     * @param column The column to get the block from
     * @return The block at the position, or null if there is no block
     */
    public Block getBlock(int row, int column) {
        if (isValidPosition(row, column)) {
            return cells[row][column];
        }
        return null;
    }

    /**
     * Clears the grid.
     */
    public void clear() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                cells[row][column] = null;
            }
        }
    }

    /**
     * Clears the specified row.
     *
     * @param row The row to clear
     */
    public void clearRow(int row) {
        if (row >= 0 && row < rows) {
            for (int column = 0; column < columns; column++) {
                cells[row][column] = null;
            }
        }
    }

    /**
     * Moves all blocks above the specified row down by one row.
     *
     * @param row The row to move blocks down from
     */
    public void moveBlocksDown(int row) {
        if (row >= 0 && row < rows) {
            for (int r = row; r > 0; r--) {
                for (int column = 0; column < columns; column++) {
                    cells[r][column] = cells[r - 1][column];
                    if (cells[r][column] != null) {
                        cells[r][column].setPosition(r, column);
                    }
                }
            }

            // Clear the top row
            for (int column = 0; column < columns; column++) {
                cells[0][column] = null;
            }
        }
    }

    /**
     * Checks if a row is full.
     *
     * @param row The row to check
     * @return True if the row is full, false otherwise
     */
    public boolean isRowFull(int row) {
        if (row >= 0 && row < rows) {
            for (int column = 0; column < columns; column++) {
                if (cells[row][column] == null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if a row is empty.
     *
     * @param row The row to check
     * @return True if the row is empty, false otherwise
     */
    public boolean isRowEmpty(int row) {
        if (row >= 0 && row < rows) {
            for (int column = 0; column < columns; column++) {
                if (cells[row][column] != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the entire grid is empty.
     *
     * @return True if the grid is empty, false otherwise
     */
    public boolean isGridEmpty() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (cells[row][column] != null) {
                    return false; // Found a non-empty cell, so the grid is not empty
                }
            }
        }
        return true; // All cells are empty
    }

    /**
     * Renders the grid to the specified graphics context.
     *
     * @param g The graphics context to render to
     */
    public void render(Graphics g) {
        // Draw the grid background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(xOffset, yOffset, columns * cellSize, rows * cellSize);

        // Draw the grid lines
        g.setColor(Color.GRAY);
        for (int row = 0; row <= rows; row++) {
            g.drawLine(xOffset, yOffset + row * cellSize,
                    xOffset + columns * cellSize, yOffset + row * cellSize);
        }

        for (int column = 0; column <= columns; column++) {
            g.drawLine(xOffset + column * cellSize, yOffset,
                    xOffset + column * cellSize, yOffset + rows * cellSize);
        }

        // Draw the blocks
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (cells[row][column] != null) {
                    cells[row][column].render(g, xOffset + column * cellSize, yOffset + row * cellSize, cellSize);
                }
            }
        }
    }

    /**
     * Gets the number of rows in the grid.
     *
     * @return The number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the number of columns in the grid.
     *
     * @return The number of columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Gets the size of each cell in pixels.
     *
     * @return The cell size
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Gets the X offset of the grid in pixels.
     *
     * @return The X offset
     */
    public int getXOffset() {
        return xOffset;
    }

    /**
     * Gets the Y offset of the grid in pixels.
     *
     * @return The Y offset
     */
    public int getYOffset() {
        return yOffset;
    }

    /**
     * Applies gravity to make blocks fall into empty spaces.
     */
    public void applyGravity() {
        // For each column
        for (int col = 0; col < columns; col++) {
            // Start from the bottom row
            for (int row = rows - 1; row >= 0; row--) {
                // If the cell is empty
                if (!isOccupied(row, col)) {
                    // Find the first non-empty cell above
                    int aboveRow = row - 1;
                    while (aboveRow >= 0) {
                        if (isOccupied(aboveRow, col)) {
                            // Move the block down
                            Block block = removeBlock(aboveRow, col);
                            placeBlock(block, row, col);
                            break;
                        }
                        aboveRow--;
                    }
                }
            }
        }
    }

    /**
     * Finds all connected blocks of the same color using BFS.
     * 
     * @param startRow    The starting row
     * @param startCol    The starting column
     * @param targetColor The color to match
     * @return Set of Points representing connected blocks
     */
    public Set<Point> findConnectedBlocks(int startRow, int startCol, Color targetColor) {
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
                if (isValidPosition(newRow, newCol) &&
                        isOccupied(newRow, newCol) &&
                        !visited.contains(newPoint)) {

                    Block block = getBlock(newRow, newCol);

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
}