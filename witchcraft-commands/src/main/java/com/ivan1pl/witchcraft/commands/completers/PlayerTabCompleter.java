package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default tab completer for {@link Player} type.
 */
@Managed
public class PlayerTabCompleter implements TabCompleter {
    private final JavaPlugin javaPlugin;

    public PlayerTabCompleter(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    /**
     * Get suggestions based on partial player name.
     * @param partial partial parameter value
     * @return set of player names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        return javaPlugin.getServer().matchPlayer(partial).stream()
                .map(HumanEntity::getName)
                .collect(Collectors.toSet());
    }
}
