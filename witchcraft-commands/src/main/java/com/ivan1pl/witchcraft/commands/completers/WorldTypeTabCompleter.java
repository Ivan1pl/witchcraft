package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.WorldType;

import java.util.HashSet;
import java.util.Set;

/**
 * Default tab completer for {@link WorldType} type.
 */
@Managed
public class WorldTypeTabCompleter implements TabCompleter {
    /**
     * Get suggestions based on partial world type name.
     * @param partial partial parameter value
     * @return set of world type names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        Set<String> result = new HashSet<>();
        partial = partial.toLowerCase();
        for (WorldType worldType : WorldType.values()) {
            if (worldType.getName().toLowerCase().startsWith(partial)) {
                result.add(worldType.getName().toLowerCase());
            }
        }
        return result;
    }
}
