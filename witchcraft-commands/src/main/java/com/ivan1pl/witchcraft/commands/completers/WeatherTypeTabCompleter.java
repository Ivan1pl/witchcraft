package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.WeatherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Default tab completer for {@link WeatherType} type.
 */
@Managed
public class WeatherTypeTabCompleter implements TabCompleter {
    /**
     * Get suggestions based on partial weather type name.
     * @param partial partial parameter value
     * @return set of weather type names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        Set<String> result = new HashSet<>();
        partial = partial.toLowerCase();
        for (WeatherType weatherType : WeatherType.values()) {
            if (weatherType.name().toLowerCase().startsWith(partial)) {
                result.add(weatherType.name().toLowerCase());
            }
        }
        return result;
    }
}
