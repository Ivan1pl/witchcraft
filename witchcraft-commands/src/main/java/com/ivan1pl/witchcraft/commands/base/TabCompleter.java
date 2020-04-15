package com.ivan1pl.witchcraft.commands.base;

import java.util.Set;

/**
 * Tab completer interface used to get tab completion suggestion for a command parameter.
 */
public interface TabCompleter {
    /**
     * Get tab suggestions.
     * @param partial partial parameter value
     * @return all possible values matching given partial value
     */
    Set<String> getSuggestions(String partial);
}
