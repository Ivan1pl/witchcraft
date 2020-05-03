package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * Default tab completer for {@link Material} type.
 */
@Managed
public class MaterialTabCompleter implements TabCompleter {
    /**
     * Get suggestions based on partial material name.
     * @param partial partial parameter value
     * @return set of material names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        Set<String> result = new HashSet<>();
        partial = partial.toLowerCase();
        for (Material material : Material.values()) {
            try {
                if (material.getKey().getKey().toLowerCase().startsWith(partial) ||
                        ("minecraft:" + material.getKey().getKey().toLowerCase()).startsWith(partial)) {
                    result.add("minecraft:" + material.getKey().getKey().toLowerCase());
                }
            } catch (IllegalArgumentException e) {
                //nop
            }
        }
        return result;
    }
}
