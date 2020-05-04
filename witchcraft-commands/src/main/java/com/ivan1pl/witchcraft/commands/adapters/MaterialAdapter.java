package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.Material;

/**
 * Default type adapter for {@link Material} type.
 */
@Managed
public class MaterialAdapter implements TypeAdapter {
    /**
     * Convert string to {@link Material}.
     * @param arg value to convert
     * @return {@link Material} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        if (arg.startsWith("minecraft:")) {
            arg = arg.substring(10);
        }
        try {
            return Material.getMaterial(arg.toUpperCase(), false);
        } catch (Exception e) {
            return null;
        }
    }
}
