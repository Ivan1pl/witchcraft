package com.ivan1pl.witchcraft.core.messages;

import com.ivan1pl.witchcraft.core.messages.actions.ClickEvent;
import com.ivan1pl.witchcraft.core.messages.actions.HoverEvent;

import java.util.List;

/**
 * Represents base chat component object.
 */
public abstract class BaseRawMessage {
    /**
     * The color to render this text in. Valid values are "black", "dark_blue", "dark_green", "dark_aqua", "dark_red",
     * "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white",
     * and "reset" (cancels out the effects of colors used by parent objects). Technically, "bold", "italic",
     * "underlined", "strikethrough", and "obfuscated" are also accepted, but it may be better practice to use the tags
     * below for such formats.
     */
    private ChatColor color;

    /**
     * Boolean (true/false) - whether to render text in bold. Defaults to false.
     */
    private Boolean bold;

    /**
     * Boolean (true/false) - whether to render text in italics. Defaults to false.
     */
    private Boolean italic;

    /**
     * Boolean (true/false) - whether to render text underlined. Defaults to false.
     */
    private Boolean underlined;

    /**
     * Boolean (true/false) - whether to render text with a strikethrough. Defaults to false.
     */
    private Boolean strikethrough;

    /**
     * Boolean (true/false) - whether to render text obfuscated. Defaults to false.
     */
    private Boolean obfuscated;

    /**
     * When the text is shift-clicked by a player, this string is inserted in their chat input. It does not overwrite
     * any existing text the player was writing.
     */
    private String insertion;

    /**
     * Allows for events to occur when the player clicks on text.
     */
    private ClickEvent clickEvent;

    /**
     * Allows for a tooltip to be displayed when the player hovers their mouse over text.
     */
    private HoverEvent hoverEvent;

    /**
     * A list of additional objects, sharing the same format as the base object.
     */
    private List<BaseRawMessage> extra;

    /**
     * Get color.
     * @return color
     */
    public ChatColor getColor() {
        return color;
    }

    /**
     * Set color.
     * <p>
     * The color to render this text in. Valid values are "black", "dark_blue", "dark_green", "dark_aqua", "dark_red",
     * "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white",
     * and "reset" (cancels out the effects of colors used by parent objects). Technically, "bold", "italic",
     * "underlined", "strikethrough", and "obfuscated" are also accepted, but it may be better practice to use the tags
     * below for such formats.
     *
     * @param color color to set
     */
    public void setColor(ChatColor color) {
        this.color = color;
    }

    /**
     * Get bold modifier.
     * @return bold modifier
     */
    public Boolean getBold() {
        return bold;
    }

    /**
     * Set bold modifier.
     * <p>
     * Boolean (true/false) - whether to render text in bold. Defaults to false.
     *
     * @param bold bold modifier to set
     */
    public void setBold(Boolean bold) {
        this.bold = bold;
    }

    /**
     * Get italic modifier.
     * @return italic modifier
     */
    public Boolean getItalic() {
        return italic;
    }

    /**
     * Set italic modifier.
     * <p>
     * Boolean (true/false) - whether to render text in italics. Defaults to false.
     *
     * @param italic italic modifier to set
     */
    public void setItalic(Boolean italic) {
        this.italic = italic;
    }

    /**
     * Get underlined modifier.
     * @return underlined modifier
     */
    public Boolean getUnderlined() {
        return underlined;
    }

    /**
     * Set underlined modifier.
     * <p>
     * Boolean (true/false) - whether to render text underlined. Defaults to false.
     *
     * @param underlined underlined modifier to set
     */
    public void setUnderlined(Boolean underlined) {
        this.underlined = underlined;
    }

    /**
     * Get strikethrough modifier.
     * @return strikethrough modifier
     */
    public Boolean getStrikethrough() {
        return strikethrough;
    }

    /**
     * Set strikethrough modifier.
     * <p>
     * Boolean (true/false) - whether to render text with a strikethrough. Defaults to false.
     *
     * @param strikethrough strikethrough modifier to set
     */
    public void setStrikethrough(Boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    /**
     * Get obfuscated modifier.
     * @return obfuscated modifier
     */
    public Boolean getObfuscated() {
        return obfuscated;
    }

    /**
     * Set obfuscated modifier.
     * <p>
     * Boolean (true/false) - whether to render text obfuscated. Defaults to false.
     *
     * @param obfuscated obfuscated modifier to set
     */
    public void setObfuscated(Boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    /**
     * Get insertion.
     * @return insertion
     */
    public String getInsertion() {
        return insertion;
    }

    /**
     * Set insertion.
     * <p>
     * When the text is shift-clicked by a player, this string is inserted in their chat input. It does not overwrite
     * any existing text the player was writing.
     *
     * @param insertion insertion to set
     */
    public void setInsertion(String insertion) {
        this.insertion = insertion;
    }

    /**
     * Get click event.
     * @return click event
     */
    public ClickEvent getClickEvent() {
        return clickEvent;
    }

    /**
     * Set click event.
     * <p>
     * Allows for events to occur when the player clicks on text.
     *
     * @param clickEvent click event to set
     */
    public void setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    /**
     * Get hover event.
     * @return hover event
     */
    public HoverEvent getHoverEvent() {
        return hoverEvent;
    }

    /**
     * Set hover event.
     * <p>
     * Allows for a tooltip to be displayed when the player hovers their mouse over text.
     *
     * @param hoverEvent hover event to set
     */
    public void setHoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
    }

    /**
     * Get extra.
     * @return extra
     */
    public List<BaseRawMessage> getExtra() {
        return extra;
    }

    /**
     * Set extra.
     * <p>
     * A list of additional objects, sharing the same format as the base object.
     *
     * @param extra extra to set
     */
    public void setExtra(List<BaseRawMessage> extra) {
        this.extra = extra;
    }

    /**
     * Check if message content is empty.
     * @return {@code true} iff message content is empty
     */
    public abstract boolean isEmpty();

    /**
     * Copy common formatting properties from another component to this chat component.
     * @param from component to copy from
     */
    protected final void copyFormattingFrom(BaseRawMessage from) {
        if (from != null) {
            this.color = from.color;
            this.bold = from.bold;
            this.italic = from.italic;
            this.underlined = from.underlined;
            this.strikethrough = from.strikethrough;
            this.obfuscated = from.obfuscated;
            this.insertion = from.insertion;
            this.clickEvent = from.clickEvent;
            this.hoverEvent = from.hoverEvent;
            this.extra = from.extra;
        }
    }
}
