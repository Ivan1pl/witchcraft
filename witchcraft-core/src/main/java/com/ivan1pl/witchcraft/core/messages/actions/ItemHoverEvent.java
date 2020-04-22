package com.ivan1pl.witchcraft.core.messages.actions;

/**
 * Represents item tooltip to be displayed when the player hovers their mouse over text.
 */
public class ItemHoverEvent extends HoverEvent {
    /**
     * Can be a string formatted like item NBT data. Contains the "id" tag, and optionally the "Damage" tag and "tag"
     * tag (which is the same compound used as "dataTag" in the {@code /give} command).
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
     * Can be a string formatted like item NBT data. Contains the "id" tag, and optionally the "Damage" tag and "tag"
     * tag (which is the same compound used as "dataTag" in the {@code /give} command).
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
    public ItemHoverEvent(String value) {
        super(Action.SHOW_ITEM);
        this.value = value;
    }
}
