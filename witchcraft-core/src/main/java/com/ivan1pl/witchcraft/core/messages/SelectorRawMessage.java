package com.ivan1pl.witchcraft.core.messages;

/**
 * Represents selector chat component object.
 */
public class SelectorRawMessage extends BaseRawMessage {
    /**
     * A string containing a selector ({@code @p},{@code @a},{@code @r},{@code @e} or {@code @s}) and, optionally,
     * selector arguments. Unlike {@link TextRawMessage#setText}, the selector is translated into the correct
     * player/entity names. If more than one player/entity is detected by the selector, it is displayed in a form such
     * as 'Name1 and Name2' or 'Name1, Name2, Name3, and Name4'.
     *
     * Clicking a player's name inserted into a {@code /tellraw} command this way suggests a command to whisper to that
     * player. Shift-clicking a player's name inserts that name into chat. Shift-clicking a non-player entity's name
     * inserts its UUID into chat.
     */
    private String selector;

    /**
     * Get selector.
     * @return selector
     */
    public String getSelector() {
        return selector;
    }

    /**
     * Set selector.
     *
     * A string containing a selector ({@code @p},{@code @a},{@code @r},{@code @e} or {@code @s}) and, optionally,
     * selector arguments. Unlike {@link TextRawMessage#setText}, the selector is translated into the correct
     * player/entity names. If more than one player/entity is detected by the selector, it is displayed in a form such
     * as 'Name1 and Name2' or 'Name1, Name2, Name3, and Name4'.
     *
     * Clicking a player's name inserted into a {@code /tellraw} command this way suggests a command to whisper to that
     * player. Shift-clicking a player's name inserts that name into chat. Shift-clicking a non-player entity's name
     * inserts its UUID into chat.
     *
     * @param selector selector to set
     */
    public void setSelector(String selector) {
        this.selector = selector;
    }

    /**
     * Check if this selector chat component's content is empty.
     * @return {@code true} iff message content is empty
     */
    @Override
    public boolean isEmpty() {
        return selector == null || selector.isEmpty();
    }

    /**
     * Create new instance with formatting copied from other component.
     * @param from component to copy from
     * @return new component instance
     */
    public static SelectorRawMessage from(BaseRawMessage from) {
        SelectorRawMessage selectorRawMessage = new SelectorRawMessage();
        selectorRawMessage.copyFormattingFrom(from);
        return selectorRawMessage;
    }
}
