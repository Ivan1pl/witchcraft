package com.ivan1pl.witchcraft.core.messages;

/**
 * Represents score chat component object.
 */
public class ScoreRawMessage extends BaseRawMessage {
    /**
     * A player's score in an objective. Displays nothing if the player is not tracked in the given objective.
     */
    private Score score;

    /**
     * Get score.
     * @return score
     */
    public Score getScore() {
        return score;
    }

    /**
     * Set score.
     *
     * A player's score in an objective. Displays nothing if the player is not tracked in the given objective.
     *
     * @param score score to set
     */
    public void setScore(Score score) {
        this.score = score;
    }

    /**
     * Check if this score chat component's content is empty.
     * @return {@code true} iff message content is empty
     */
    @Override
    public boolean isEmpty() {
        return score == null;
    }

    /**
     * Create new instance with formatting copied from other component.
     * @param from component to copy from
     * @return new component instance
     */
    public static ScoreRawMessage from(BaseRawMessage from) {
        ScoreRawMessage scoreRawMessage = new ScoreRawMessage();
        scoreRawMessage.copyFormattingFrom(from);
        return scoreRawMessage;
    }
}
