package com.ivan1pl.witchcraft.core.builders;

import org.bukkit.ChatColor;

/**
 * Builder class for complex messages.
 */
public class MessageBuilder {
    private final StringBuilder builder = new StringBuilder();

    /**
     * Set message color.
     * @param chatColor new color
     * @return builder instance
     */
    public MessageBuilder color(ChatColor chatColor) {
        builder.append(chatColor.toString());
        return this;
    }

    /**
     * Set message color.
     * @param chatColor new color
     * @return builder instance
     */
    public MessageBuilder color(com.ivan1pl.witchcraft.core.messages.ChatColor chatColor) {
        builder.append(chatColor.getBukkitColor().toString());
        return this;
    }

    /**
     * Reset message color to default.
     * @return builder instance
     */
    public MessageBuilder resetColor() {
        builder.append(ChatColor.RESET.toString());
        return this;
    }

    /**
     * Reset all formatting.
     * @return builder instance
     */
    public MessageBuilder reset() {
        return resetColor();
    }

    /**
     * Append text.
     * @param value text to append
     * @return builder instance
     */
    public MessageBuilder append(String value) {
        builder.append(value);
        return this;
    }

    /**
     * Break line.
     * @return builder instance
     */
    public MessageBuilder newLine() {
        builder.append("\n");
        return this;
    }

    /**
     * Build message.
     * @return message
     */
    public String build() {
        return builder.toString();
    }
}
