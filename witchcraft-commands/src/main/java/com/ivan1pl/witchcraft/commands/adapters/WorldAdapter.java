package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Default type adapter for {@link World} type.
 */
@Managed
public class WorldAdapter implements TypeAdapter {
    private final JavaPlugin javaPlugin;

    public WorldAdapter(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    /**
     * Convert string to {@link World}.
     * @param arg value to convert
     * @return {@link World} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        List<World> worlds = javaPlugin.getServer().getWorlds();
        for (World world : worlds) {
            if (world.getName().equalsIgnoreCase(arg)) {
                return world;
            }
        }
        return null;
    }
}
