package com.ivan1pl.witchcraft.core.messages.actions;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a tooltip to be displayed when the player hovers their mouse over text.
 */
public abstract class HoverEvent {
    /**
     * The type of tooltip to show.
     */
    private final Action action;

    protected HoverEvent(Action action) {
        this.action = action;
    }

    /**
     * Get action.
     * <p>
     * The type of tooltip to show.
     *
     * @return action
     */
    public Action getAction() {
        return action;
    }

    /**
     * All possible tooltip types.
     */
    public enum Action {
        /**
         * Shows raw JSON text.
         */
        @SerializedName("show_text") SHOW_TEXT,
        /**
         * Shows the tooltip of an item that can have NBT tags.
         */
        @SerializedName("show_item") SHOW_ITEM,
        /**
         * Shows an entity's name, possibly its type, and its UUID.
         */
        @SerializedName("show_entity") SHOW_ENTITY,
        ;
    }
}
