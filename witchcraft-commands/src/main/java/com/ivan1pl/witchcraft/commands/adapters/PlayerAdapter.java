package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Default type adapter for {@link Player} type.
 */
@Managed
public class PlayerAdapter implements TypeAdapter {
    private final JavaPlugin javaPlugin;

    public PlayerAdapter(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    /**
     * Convert string to {@link Player}.
     * @param arg value to convert
     * @return {@link Player} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        Player player = javaPlugin.getServer().getPlayer(arg);
        if (player != null) {
            return player;
        } else {
            OfflinePlayer[] players = javaPlugin.getServer().getOfflinePlayers();
            for (OfflinePlayer offlinePlayer : players) {
                if (arg.equalsIgnoreCase(offlinePlayer.getName())) {
                    return offlinePlayer.getPlayer();
                }
            }
            return null;
        }
    }
}
