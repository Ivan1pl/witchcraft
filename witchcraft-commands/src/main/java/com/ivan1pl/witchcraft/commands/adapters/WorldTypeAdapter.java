package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.WorldType;

/**
 * Default type adapter for {@link WorldType} type.
 */
@Managed
public class WorldTypeAdapter implements TypeAdapter {
    /**
     * Convert string to {@link WorldType}.
     * @param arg value to convert
     * @return {@link WorldType} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        arg = arg.toLowerCase();
        try {
            return WorldType.getByName(arg.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
