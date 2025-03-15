package tilematch;

/**
 * The Timer class handles timing-related functionality for the game.
 * It tracks elapsed time and can trigger events at specified intervals.
 */
public class Timer {
    private double elapsedTime;
    private double gameSpeed;
    private double dropInterval;
    private double lastDropTime;

    /**
     * Creates a new Timer with default settings.
     */
    public Timer() {
        this.elapsedTime = 0;
        this.gameSpeed = 1.0;
        this.dropInterval = 1.0;
        this.lastDropTime = 0;
    }

    /**
     * Updates the timer with the elapsed time.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     */
    public void update(double deltaTime) {
        elapsedTime += deltaTime;
    }

    /**
     * Checks if it's time to drop a block based on the drop interval.
     *
     * @return True if it's time to drop a block, false otherwise
     */
    public boolean shouldDropBlock() {
        if (elapsedTime - lastDropTime >= dropInterval / gameSpeed) {
            lastDropTime = elapsedTime;
            return true;
        }
        return false;
    }

    /**
     * Gets the elapsed time.
     *
     * @return The elapsed time in seconds
     */
    public double getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Sets the game speed.
     *
     * @param gameSpeed The new game speed
     */
    public void setGameSpeed(double gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

    /**
     * Gets the current game speed.
     *
     * @return The current game speed
     */
    public double getGameSpeed() {
        return gameSpeed;
    }

    /**
     * Sets the drop interval.
     *
     * @param dropInterval The new drop interval in seconds
     */
    public void setDropInterval(double dropInterval) {
        this.dropInterval = dropInterval;
    }

    /**
     * Gets the current drop interval.
     *
     * @return The current drop interval in seconds
     */
    public double getDropInterval() {
        return dropInterval;
    }
}