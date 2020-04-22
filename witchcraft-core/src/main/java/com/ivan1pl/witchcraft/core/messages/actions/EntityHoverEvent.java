package com.ivan1pl.witchcraft.core.messages.actions;

/**
 * Represents entity tooltip to be displayed when the player hovers their mouse over text.
 */
public class EntityHoverEvent extends HoverEvent {
    /**
     * Can be string formatted like a compound with the string values "type" (such as "Zombie"), "name", and "id"
     * (should be an entity UUID, but can actually be any string).
     */
    private String value;

    /**
     * Get value.
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set value.
     *
     * Can be string formatted like a compound with the string values "type" (such as "Zombie"), "name", and "id"
     * (should be an entity UUID, but can actually be any string).
     *
     * @param value value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Constructor.
     * @param value value
     */
    public EntityHoverEvent(String value) {
        super(Action.SHOW_ENTITY);
        this.value = value;
    }
}
