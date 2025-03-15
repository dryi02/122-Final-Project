package tilematch;

import java.awt.Color;
import java.awt.Graphics;

/**
 * The Block class represents a single block in the game.
 * It can be extended to create different types of blocks for different games.
 */
public class Block {
    protected int row;
    protected int column;
    protected Color color;
    protected BlockType type;

    /**
     * Creates a new Block with the specified type and color.
     *
     * @param type  The type of the block
     * @param color The color of the block
     */
    public Block(BlockType type, Color color) {
        this.type = type;
        this.color = color;
        this.row = 0;
        this.column = 0;
    }

    /**
     * Creates a new Block with the specified position, type, and color.
     *
     * @param row    The row position of the block
     * @param column The column position of the block
     * @param type   The type of the block
     * @param color  The color of the block
     */
    public Block(int row, int column, BlockType type, Color color) {
        this.row = row;
        this.column = column;
        this.type = type;
        this.color = color;
    }

    /**
     * Sets the position of the block.
     *
     * @param row    The row position
     * @param column The column position
     */
    public void setPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Gets the row position of the block.
     *
     * @return The row position
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column position of the block.
     *
     * @return The column position
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the type of the block.
     *
     * @return The block type
     */
    public BlockType getType() {
        return type;
    }

    /**
     * Gets the color of the block.
     *
     * @return The block color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the block.
     *
     * @param color The new color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Renders the block to the specified graphics context.
     *
     * @param g        The graphics context to render to
     * @param x        The x-coordinate to render at
     * @param y        The y-coordinate to render at
     * @param cellSize The size of the cell in pixels
     */
    public void render(Graphics g, int x, int y, int cellSize) {
        // Draw the block background
        g.setColor(color);
        g.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);

        // Draw the block border
        g.setColor(Color.BLACK);
        g.drawRect(x + 1, y + 1, cellSize - 2, cellSize - 2);

        // Draw highlights
        g.setColor(new Color(255, 255, 255, 100));
        g.drawLine(x + 2, y + 2, x + cellSize - 3, y + 2);
        g.drawLine(x + 2, y + 2, x + 2, y + cellSize - 3);

        // Draw shadows
        g.setColor(new Color(0, 0, 0, 100));
        g.drawLine(x + cellSize - 3, y + 2, x + cellSize - 3, y + cellSize - 3);
        g.drawLine(x + 2, y + cellSize - 3, x + cellSize - 3, y + cellSize - 3);
    }

    /**
     * Checks if this block can match with another block.
     *
     * @param other The other block to check
     * @return True if the blocks can match, false otherwise
     */
    public boolean canMatch(Block other) {
        if (other == null) {
            return false;
        }
        return this.type == other.type;
    }

    /**
     * Enum representing different types of blocks.
     */
    public enum BlockType {
        STANDARD,
        SPECIAL,
        BOMB,
        HORIZONTAL_CLEAR,
        VERTICAL_CLEAR,
        COLOR_BOMB
    }
}