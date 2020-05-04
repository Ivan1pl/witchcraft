package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;

/**
 * Default type adapter for {@code double} and {@link Double} types.
 */
@Managed
public class DoubleAdapter implements TypeAdapter {
    /**
     * Convert string to double.
     * @param arg value to convert
     * @return double representation of given argument
     */
    @Override
    public Object convert(String arg) {
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
