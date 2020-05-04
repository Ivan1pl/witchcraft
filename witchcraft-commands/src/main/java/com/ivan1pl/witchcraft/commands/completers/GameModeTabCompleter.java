package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.GameMode;

import java.util.HashSet;
import java.util.Set;

/**
 * Default tab completer for {@link GameMode} type.
 */
@Managed
public class GameModeTabCompleter implements TabCompleter {
    /**
     * Get suggestions based on partial game mode name.
     * @param partial partial parameter value
     * @return set of game mode names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        Set<String> result = new HashSet<>();
        partial = partial.toLowerCase();
        for (GameMode gameMode : GameMode.values()) {
            if (gameMode.name().toLowerCase().startsWith(partial)) {
                result.add(gameMode.name().toLowerCase());
            }
        }
        return result;
    }
}
