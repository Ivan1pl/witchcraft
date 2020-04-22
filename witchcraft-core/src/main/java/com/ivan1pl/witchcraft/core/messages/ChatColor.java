package com.ivan1pl.witchcraft.core.messages;

import com.google.gson.annotations.SerializedName;

/**
 * All possible chat colors.
 */
public enum ChatColor {
    /**
     * Represents black.
     */
    @SerializedName("black") BLACK("black", org.bukkit.ChatColor.BLACK),
    /**
     * Represents dark blue.
     */
    @SerializedName("dark_blue") DARK_BLUE("dark_blue", org.bukkit.ChatColor.DARK_BLUE),
    /**
     * Represents dark green.
     */
    @SerializedName("dark_green") DARK_GREEN("dark_green", org.bukkit.ChatColor.DARK_GREEN),
    /**
     * Represents dark blue (aqua).
     */
    @SerializedName("dark_aqua") DARK_AQUA("dark_aqua", org.bukkit.ChatColor.DARK_AQUA),
    /**
     * Represents dark red.
     */
    @SerializedName("dark_red") DARK_RED("dark_red", org.bukkit.ChatColor.DARK_RED),
    /**
     * Represents dark purple.
     */
    @SerializedName("dark_purple") DARK_PURPLE("dark_purple", org.bukkit.ChatColor.DARK_PURPLE),
    /**
     * Represents gold.
     */
    @SerializedName("gold") GOLD("gold", org.bukkit.ChatColor.GOLD),
    /**
     * Represents gray.
     */
    @SerializedName("gray") GRAY("gray", org.bukkit.ChatColor.GRAY),
    /**
     * Represents dark gray.
     */
    @SerializedName("dark_gray") DARK_GRAY("dark_gray", org.bukkit.ChatColor.DARK_GRAY),
    /**
     * Represents blue.
     */
    @SerializedName("blue") BLUE("blue", org.bukkit.ChatColor.BLUE),
    /**
     * Represents green.
     */
    @SerializedName("green") GREEN("green", org.bukkit.ChatColor.GREEN),
    /**
     * Represents aqua.
     */
    @SerializedName("aqua") AQUA("aqua", org.bukkit.ChatColor.AQUA),
    /**
     * Represents red.
     */
    @SerializedName("red") RED("red", org.bukkit.ChatColor.RED),
    /**
     * Represents light purple.
     */
    @SerializedName("light_purple") LIGHT_PURPLE("light_purple", org.bukkit.ChatColor.LIGHT_PURPLE),
    /**
     * Represents yellow.
     */
    @SerializedName("yellow") YELLOW("yellow", org.bukkit.ChatColor.YELLOW),
    /**
     * Represents white.
     */
    @SerializedName("white") WHITE("white", org.bukkit.ChatColor.WHITE),
    /**
     * Represents magical characters that change around randomly.
     */
    @Deprecated
    @SerializedName("obfuscated") MAGIC("obfuscated", org.bukkit.ChatColor.MAGIC),
    /**
     * Makes the text bold.
     */
    @Deprecated
    @SerializedName("bold") BOLD("bold", org.bukkit.ChatColor.BOLD),
    /**
     * Makes a line appear through the text.
     */
    @Deprecated
    @SerializedName("strikethrough") STRIKETHROUGH("strikethrough", org.bukkit.ChatColor.STRIKETHROUGH),
    /**
     * Makes the text appear underlined.
     */
    @Deprecated
    @SerializedName("underline") UNDERLINE("underline", org.bukkit.ChatColor.UNDERLINE),
    /**
     * Makes the text italic.
     */
    @Deprecated
    @SerializedName("italic") ITALIC("italic", org.bukkit.ChatColor.ITALIC),
    /**
     * Resets all previous chat colors or formats.
     */
    @SerializedName("reset") RESET("reset", org.bukkit.ChatColor.RESET),
    ;

    /**
     * Color key.
     */
    private String key;

    /**
     * Bukkit representation.
     */
    private org.bukkit.ChatColor bukkitColor;

    ChatColor(String key, org.bukkit.ChatColor bukkitColor) {
        this.key = key;
        this.bukkitColor = bukkitColor;
    }

    /**
     * Get Bukkit representation.
     * @return Bukkit representation
     */
    public org.bukkit.ChatColor getBukkitColor() {
        return bukkitColor;
    }

    /**
     * Get color key.
     * @return color key
     */
    public String getKey() {
        return key;
    }
}
