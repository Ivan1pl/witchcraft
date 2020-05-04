package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.Instrument;

/**
 * Default type adapter for {@link Instrument} type.
 */
@Managed
public class InstrumentAdapter implements TypeAdapter {
    /**
     * Convert string to {@link Instrument}.
     * @param arg value to convert
     * @return {@link Instrument} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        arg = arg.toLowerCase();
        try {
            return Instrument.valueOf(arg.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
