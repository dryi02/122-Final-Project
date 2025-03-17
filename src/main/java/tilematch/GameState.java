package tilematch;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

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
    
    public void switchPlayers() {
    	if(this.currPlayerIndex == 0) {
    		this.currPlayerIndex = 1;
    	}else {
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
    	for(int i = 0; i<players.size(); i++) {
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
}