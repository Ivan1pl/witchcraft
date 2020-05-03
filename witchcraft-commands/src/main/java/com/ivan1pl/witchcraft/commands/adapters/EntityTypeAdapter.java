package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.entity.EntityType;

/**
 * Default type adapter for {@link EntityType} type.
 */
@Managed
public class EntityTypeAdapter implements TypeAdapter {
    /**
     * Convert string to {@link EntityType}.
     * @param arg value to convert
     * @return {@link EntityType} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        if (arg.startsWith("minecraft:")) {
            arg = arg.substring(10);
        }
        try {
            return EntityType.fromName(arg);
        } catch (Exception e) {
            return null;
        }
    }
}
