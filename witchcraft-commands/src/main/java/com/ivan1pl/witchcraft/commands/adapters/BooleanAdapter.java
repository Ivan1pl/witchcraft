package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;

/**
 * Default type adapter for {@code boolean} and {@link Boolean} types.
 */
@Managed
public class BooleanAdapter implements TypeAdapter {
    /**
     * Convert string to boolean.
     * @param arg value to convert
     * @return boolean representation of given argument
     */
    @Override
    public Object convert(String arg) {
        if ("1".equals(arg) || "t".equalsIgnoreCase(arg) || "true".equalsIgnoreCase(arg) || "y".equalsIgnoreCase(arg) ||
                "yes".equalsIgnoreCase(arg)) {
            return true;
        } else if ("0".equals(arg) || "f".equalsIgnoreCase(arg) || "false".equalsIgnoreCase(arg) ||
                "n".equalsIgnoreCase(arg) || "no".equalsIgnoreCase(arg)) {
            return false;
        } else {
            return null;
        }
    }
}
