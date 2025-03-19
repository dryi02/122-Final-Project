package tilematch;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * The GameState class manages the current state of the game.
 * It handles game logic, updates, and rendering.
 */
public abstract class GameState {
    protected Grid grid;
    protected List<Player> players = new ArrayList<Player>();
    protected Player activePlayer;
    protected Timer timer;
    protected List<Block> activeBlocks;
    protected boolean gameOver;
    protected int currPlayerIndex = 0;
    protected Display display;

    /**
     * Creates a new GameState with the specified grid dimensions.
     *
     * @param rows    The number of rows in the grid
     * @param columns The number of columns in the grid
     */
    public GameState(int rows, int columns) {
        this.grid = new Grid(rows, columns);
        Player playerOne = new Player("Player 1");
        Player playerTwo = new Player("Player 2");
        this.players.add(playerOne);
        this.players.add(playerTwo);
        this.activePlayer = players.get(currPlayerIndex);
        this.timer = new Timer();
        this.activeBlocks = new ArrayList<>();
        this.gameOver = false;

    }

    /**
     * Updates the game state based on the elapsed time.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     */
    public void update(double deltaTime) {
        if (gameOver) {
            return;
        }

        timer.update(deltaTime);
        updateGame(deltaTime);
        checkGameOver();
    }

    /**
     * Renders the game state to the specified graphics context.
     *
     * @param g The graphics context to render to
     */
    public void render(Graphics g) {
        grid.render(g);

        // Render active blocks with proper coordinates
        for (Block block : activeBlocks) {
            int x = grid.getXOffset() + block.getColumn() * grid.getCellSize();
            int y = grid.getYOffset() + block.getRow() * grid.getCellSize();
            block.render(g, x, y, grid.getCellSize());
        }

        renderUI(g);
    }

    /**
     * Handles player input.
     *
     * @param input The input to handle
     */
    public abstract void handleInput(String input);

    /**
     * Updates the game state.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     */
    protected abstract void updateGame(double deltaTime);

    /**
     * Renders the user interface.
     *
     * @param g The graphics context to render to
     */
    protected abstract void renderUI(Graphics g);

    /**
     * Checks if the game is over.
     */
    protected abstract void checkGameOver();
    
     protected Set<Point> findConnectedBlocks(int startRow, int startCol, Color targetColor) {
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

    protected void applyGravity() {
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

    public void switchPlayers() {
        if (this.currPlayerIndex == 0) {
            this.currPlayerIndex = 1;
        } else {
            this.currPlayerIndex = 0;
        }
        this.activePlayer = players.get(currPlayerIndex);
    }

    public String getCurrPlayerName() {
        return this.activePlayer.getName();
    }

    /**
     * Gets the current score.
     *
     * @return The current score
     */
    public int getCurrPlayerScore() {
        return activePlayer.getScore();
    }

    public void addCurrPlayerScore(int score) {
        activePlayer.addScore(score);
    }

    public void resetActivePlayer() {
        activePlayer.reset();
    }

    public void resetAllPlayers() {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).reset();
        }
    }

    /**
     * Checks if the game is over.
     *
     * @return True if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }
}