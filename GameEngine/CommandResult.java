package Main.GameEngine;

/**
 * Author: Makaato Serumaga
 * Immutable result of processing a player command.
 */
public final class CommandResult {

    private final String message;
    private final boolean gameOver;
    private final boolean win;

    /**
     * Creates a new command result.
     *
     * @param message  text describing the outcome
     * @param gameOver true if the game has ended
     * @param win      true if the player has won
     */
    public CommandResult(String message, boolean gameOver, boolean win) {
        this.message = message;
        this.gameOver = gameOver;
        this.win = win;
    }

    /**
     * @return the message describing the outcome
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return true if the game has ended
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * @return true if this result represents a win
     */
    public boolean isWin() {
        return win;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "message='" + message + '\'' +
                ", gameOver=" + gameOver +
                ", win=" + win +
                '}';
    }
}
