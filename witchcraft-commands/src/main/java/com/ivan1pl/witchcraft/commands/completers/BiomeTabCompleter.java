package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.block.Biome;

import java.util.HashSet;
import java.util.Set;

/**
 * Default tab completer for {@link Biome} type.
 */
@Managed
public class BiomeTabCompleter implements TabCompleter {
    /**
     * Get suggestions based on partial biome name.
     * @param partial partial parameter value
     * @return set of biome names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        Set<String> result = new HashSet<>();
        partial = partial.toLowerCase();
        for (Biome biome : Biome.values()) {
            if (biome.name().toLowerCase().startsWith(partial) ||
                    ("minecraft:" + biome.name().toLowerCase()).startsWith(partial)) {
                result.add("minecraft:" + biome.name().toLowerCase());
            }
        }
        return result;
    }
}
