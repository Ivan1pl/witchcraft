package com.ivan1pl.witchcraft.core.messages;

/**
 * Represents score component that can be a part of base chat component object.
 */
public class Score {
    /**
     * The name of the player whose score should be displayed. Selectors (such as {@code @p}) can be used, in addition
     * to "fake" player names created by the scoreboard system. In addition, if the name is "*", it shows the reader's
     * own score (for example, {@code /tellraw @a {"score":{"name":"*","objective":"obj"}}} shows every online player
     * their own score in the "obj" objective).
     */
    private String name;

    /**
     * The internal name of the objective to display the player's score in.
     */
    private String objective;

    /**
     * Optional. If present, this value is used regardless of what the score would have been.
     */
    private String value;

    /**
     * Get name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     * <p>
     * The name of the player whose score should be displayed. Selectors (such as {@code @p}) can be used, in addition
     * to "fake" player names created by the scoreboard system. In addition, if the name is "*", it shows the reader's
     * own score (for example, {@code /tellraw @a {"score":{"name":"*","objective":"obj"}}} shows every online player
     * their own score in the "obj" objective).
     *
     * @param name name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get objective.
     * @return objective.
     */
    public String getObjective() {
        return objective;
    }

    /**
     * Set objective.
     * <p>
     * The internal name of the objective to display the player's score in.
     *
     * @param objective objective to set
     */
    public void setObjective(String objective) {
        this.objective = objective;
    }

    /**
     * Get value.
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set value.
     * <p>
     * Optional. If present, this value is used regardless of what the score would have been.
     *
     * @param value value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
