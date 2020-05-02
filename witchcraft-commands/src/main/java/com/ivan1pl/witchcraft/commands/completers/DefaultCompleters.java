package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping from class to tab completer responsible for completing parameters of that class.
 */
@Managed
public class DefaultCompleters {
    private final Map<Class<?>, TabCompleter> completers = new HashMap<>();

    /**
     * Create completers mapping.
     * @param playerTabCompleter player tab completer
     */
    public DefaultCompleters(PlayerTabCompleter playerTabCompleter) {
        completers.put(Player.class, playerTabCompleter);
    }

    /**
     * Get tab completer for given type.
     * @param requestedType requested type
     * @return tab completer associated with requested type or {@code null}
     */
    public TabCompleter get(Class<?> requestedType) {
        return completers.get(requestedType);
    }
}
