package com.ivan1pl.witchcraft.core.messages.actions;

import com.google.gson.annotations.SerializedName;

/**
 * Represents an event which occurs when the player clicks on raw message text.
 */
public class ClickEvent {
    /**
     * The action to perform when clicked.
     */
    private Action action;

    /**
     * The URL, file, chat, command or book page used by the specified {@link #action}. Note that commands must be
     * prefixed with the usual "/" slash.
     */
    private String value;

    /**
     * Constructor.
     * @param action action
     * @param value value
     */
    public ClickEvent(Action action, String value) {
        this.action = action;
        this.value = value;
    }

    /**
     * Get action.
     * @return action
     */
    public Action getAction() {
        return action;
    }

    /**
     * Set action.
     *
     * The action to perform when clicked.
     *
     * @param action action to set
     */
    public void setAction(Action action) {
        this.action = action;
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
     *
     * The URL, file, chat, command or book page used by the specified {@link #action}. Note that commands must be
     * prefixed with the usual "/" slash.
     *
     * @param value value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * All possible clickEvent actions.
     */
    public enum Action {
        /**
         * Opens {@link #value} as a URL in the player's default web browser.
         */
        @SerializedName("open_url") OPEN_URL,
        /**
         * Has {@link #value} entered in chat as though the player typed it themselves. This can be used to run
         * commands, provided the player has the required permissions.
         */
        @SerializedName("run_command") RUN_COMMAND,
        /**
         * Can be used only in written books.
         */
        @SerializedName("change_page") CHANGE_PAGE,
        /**
         * Similar to "run_command" but it cannot be used in a written book, the text appears only in the player's chat
         * input and it is not automatically entered. Unlike
         * {@link com.ivan1pl.witchcraft.core.messages.BaseRawMessage#setInsertion}, this replaces the existing contents
         * of the chat input.
         */
        @SerializedName("suggest_command") SUGGEST_COMMAND,
        /**
         * Copy the value to the clipboard.
         */
        @SerializedName("copy_to_clipboard") COPY_TO_CLIPBOARD,
        ;
    }
}
