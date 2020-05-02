package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;

/**
 * Default type adapter for {@code float} and {@link Float} types.
 */
@Managed
public class FloatAdapter implements TypeAdapter {
    /**
     * Convert string to float.
     * @param arg value to convert
     * @return float representation of given argument
     */
    @Override
    public Object convert(String arg) {
        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
