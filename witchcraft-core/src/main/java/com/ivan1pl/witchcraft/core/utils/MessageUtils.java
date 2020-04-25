package com.ivan1pl.witchcraft.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * A set of util functions dealing with messages.
 */
public class MessageUtils {
    /**
     * Send json formatted message to a player.
     * @param player player
     * @param json message
     */
    public static void sendJsonMessage(Player player, String json) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + json);
    }
}
