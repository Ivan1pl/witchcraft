package com.ivan1pl.witchcraft.core.messages;

/**
 * Represents text chat component object.
 */
public class TextRawMessage extends BaseRawMessage {
    /**
     * A string representing raw text to display directly in chat. Can use escape characters, such as {@code \n} for
     * newline (enter), {@code \t} for tab, etc.
     */
    private String text;

    /**
     * Get text.
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Set text.
     *
     * A string representing raw text to display directly in chat. Can use escape characters, such as {@code \n} for
     * newline (enter), {@code \t} for tab, etc.
     *
     * @param text text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Check if this text chat component's content is empty.
     * @return {@code true} iff message content is empty
     */
    @Override
    public boolean isEmpty() {
        return text == null || text.isEmpty();
    }

    /**
     * Create new instance with formatting copied from other component.
     * @param from component to copy from
     * @return new component instance
     */
    public static TextRawMessage from(BaseRawMessage from) {
        TextRawMessage textRawMessage = new TextRawMessage();
        textRawMessage.copyFormattingFrom(from);
        return textRawMessage;
    }
}
