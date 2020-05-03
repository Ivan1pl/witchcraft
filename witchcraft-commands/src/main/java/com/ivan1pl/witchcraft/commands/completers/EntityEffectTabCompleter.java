package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.EntityEffect;

import java.util.HashSet;
import java.util.Set;

/**
 * Default tab completer for {@link EntityEffect} type.
 */
@Managed
public class EntityEffectTabCompleter implements TabCompleter {
    /**
     * Get suggestions based on partial entity effect name.
     * @param partial partial parameter value
     * @return set of entity effect names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        Set<String> result = new HashSet<>();
        partial = partial.toLowerCase();
        for (EntityEffect entityEffect : EntityEffect.values()) {
            if (entityEffect.name().toLowerCase().startsWith(partial)) {
                result.add(entityEffect.name().toLowerCase());
            }
        }
        return result;
    }
}
