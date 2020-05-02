package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;

/**
 * Default type adapter for {@code int} and {@link Integer} types.
 */
@Managed
public class IntegerAdapter implements TypeAdapter {
    /**
     * Convert string to integer.
     * @param arg value to convert
     * @return integer representation of given argument
     */
    @Override
    public Object convert(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
