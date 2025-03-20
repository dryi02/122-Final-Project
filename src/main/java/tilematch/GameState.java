package tilematch;

import java.awt.Color;
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
 * The GameState class manages the current state of the game.
 * It handles game logic, updates, and rendering.
 */
public abstract class GameState {
    protected Grid grid;
    protected List<Player> players = new ArrayList<Player>();
    protected Player activePlayer;
    protected List<Block> activeBlocks;
    protected boolean gameOver;
    protected int currPlayerIndex = 0;
    protected Display display;
    protected String message = "";
    protected static final Random RANDOM = new Random();
    protected static final Color[] BLOCK_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
    };
    protected int selectedRow = -1;
    protected int selectedCol = -1;
    protected int swapRow = -1;
    protected int swapCol = -1;
    protected int player1Wins = 0;
    protected int player2Wins = 0;
    protected boolean swapMode = false;
    protected boolean playerOneFinished = false;

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
        this.activeBlocks = new ArrayList<>();
        this.gameOver = false;
        int[] wins = GameChooser.getPlayerWins();
        player1Wins = wins[0];
        player2Wins = wins[1];

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

//        timer.update(deltaTime);
        updateGame(deltaTime);
        gameOver = checkGameOver();
    }

    /**
     * Renders the game state to the specified graphics context.
     *
     * @param g The graphics context to render to
     */
    public void render(Graphics g) {
        grid.render(g);

        // Render active blocks with proper coordinates
        // for (Block block : activeBlocks) {
        // int x = grid.getXOffset() + block.getColumn() * grid.getCellSize();
        // int y = grid.getYOffset() + block.getRow() * grid.getCellSize();
        // block.render(g, x, y, grid.getCellSize());
        // }

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
    protected void renderUI(Graphics g) {
        renderInstructions(g);
        renderSelectionHighlight(g);
        renderSwapSelectionHighlight(g);
    }

    protected abstract void renderInstructions(Graphics g);

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

    public abstract void randomizeGrid();

    /**
     * Checks if the game is over.
     * 
     * @return
     */
    protected abstract boolean checkGameOver();

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

    public String setPlayerNames(String player1Name, String player2Name) {
        players.get(0).setName(player1Name);
        players.get(1).setName(player2Name);
        return getCurrPlayerName() + "'s Turn!";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void clearGrid() {
        grid.clear();
        resetActivePlayer();
    }
}