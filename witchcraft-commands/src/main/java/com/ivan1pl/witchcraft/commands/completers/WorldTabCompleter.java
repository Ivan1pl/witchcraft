package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default tab completer for {@link World} type.
 */
@Managed
public class WorldTabCompleter implements TabCompleter {
    private final JavaPlugin javaPlugin;

    public WorldTabCompleter(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    /**
     * Get suggestions based on partial world name.
     * @param partial partial parameter value
     * @return set of world names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        List<World> worlds = javaPlugin.getServer().getWorlds();
        Set<String> result = new HashSet<>();
        for (World world : worlds) {
            if (world.getName().toLowerCase().startsWith(partial.toLowerCase())) {
                result.add(world.getName());
            }
        }
        return result;
    }
}
