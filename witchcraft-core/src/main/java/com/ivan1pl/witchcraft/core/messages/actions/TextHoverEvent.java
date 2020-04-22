package com.ivan1pl.witchcraft.core.messages.actions;

import com.ivan1pl.witchcraft.core.messages.BaseRawMessage;

import java.util.List;

/**
 * Represents text tooltip to be displayed when the player hovers their mouse over text.
 */
public class TextHoverEvent extends HoverEvent {
    /**
     * Can be either a raw string of text or an object with the same formatting as this base object. Note that
     * {@link com.ivan1pl.witchcraft.core.messages.BaseRawMessage#setClickEvent} and
     * {@link com.ivan1pl.witchcraft.core.messages.BaseRawMessage#setHoverEvent} do not function within the tooltip, but
     * the formatting and {@link com.ivan1pl.witchcraft.core.messages.BaseRawMessage#setExtra} tags still work.
     */
    private List<BaseRawMessage> value;

    /**
     * Get value.
     * @return value
     */
    public List<BaseRawMessage> getValue() {
        return value;
    }

    /**
     * Set value.
     *
     * Can be either a raw string of text or an object with the same formatting as this base object. Note that
     * {@link com.ivan1pl.witchcraft.core.messages.BaseRawMessage#setClickEvent} and
     * {@link com.ivan1pl.witchcraft.core.messages.BaseRawMessage#setHoverEvent} do not function within the tooltip, but
     * the formatting and {@link com.ivan1pl.witchcraft.core.messages.BaseRawMessage#setExtra} tags still work.
     *
     * @param value value to set
     */
    public void setValue(List<BaseRawMessage> value) {
        this.value = value;
    }

    /**
     * Constructor.
     * @param value value
     */
    public TextHoverEvent(List<BaseRawMessage> value) {
        super(Action.SHOW_TEXT);
        this.value = value;
    }
}
