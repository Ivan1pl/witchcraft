package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.entity.EntityType;

import java.util.HashSet;
import java.util.Set;

/**
 * Default tab completer for {@link EntityType} type.
 */
@Managed
public class EntityTypeTabCompleter implements TabCompleter {
    /**
     * Get suggestions based on partial entity type name.
     * @param partial partial parameter value
     * @return set of entity type names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        Set<String> result = new HashSet<>();
        partial = partial.toLowerCase();
        for (EntityType entityType : EntityType.values()) {
            if (entityType.getName() != null && (entityType.getName().toLowerCase().startsWith(partial) ||
                    ("minecraft:" + entityType.getName().toLowerCase()).startsWith(partial))) {
                result.add("minecraft:" + entityType.getName().toLowerCase());
            }
        }
        return result;
    }
}
