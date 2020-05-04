package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.Instrument;

import java.util.HashSet;
import java.util.Set;

/**
 * Default tab completer for {@link Instrument} type.
 */
@Managed
public class InstrumentTabCompleter implements TabCompleter {
    /**
     * Get suggestions based on partial instrument name.
     * @param partial partial parameter value
     * @return set of instrument names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        Set<String> result = new HashSet<>();
        partial = partial.toLowerCase();
        for (Instrument instrument : Instrument.values()) {
            if (instrument.name().toLowerCase().startsWith(partial)) {
                result.add(instrument.name().toLowerCase());
            }
        }
        return result;
    }
}
