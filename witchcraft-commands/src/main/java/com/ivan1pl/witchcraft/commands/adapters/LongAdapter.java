package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;

/**
 * Default type adapter for {@code long} and {@link Long} types.
 */
@Managed
public class LongAdapter implements TypeAdapter {
    /**
     * Convert string to long.
     * @param arg value to convert
     * @return long representation of given argument
     */
    @Override
    public Object convert(String arg) {
        try {
            return Long.parseLong(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
