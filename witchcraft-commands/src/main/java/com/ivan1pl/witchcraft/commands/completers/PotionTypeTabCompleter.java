package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.potion.PotionType;

import java.util.HashSet;
import java.util.Set;

/**
 * Default tab completer for {@link PotionType} type.
 */
@Managed
public class PotionTypeTabCompleter implements TabCompleter {
    /**
     * Get suggestions based on partial potion type name.
     * @param partial partial parameter value
     * @return set of potion type names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        Set<String> result = new HashSet<>();
        partial = partial.toLowerCase();
        for (PotionType potionType : PotionType.values()) {
            if (potionType.name().toLowerCase().startsWith(partial)) {
                result.add(potionType.name().toLowerCase());
            }
        }
        return result;
    }
}
