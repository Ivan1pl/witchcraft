package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.block.Biome;

/**
 * Default type adapter for {@link Biome} type.
 */
@Managed
public class BiomeAdapter implements TypeAdapter {
    /**
     * Convert string to {@link Biome}.
     * @param arg value to convert
     * @return {@link Biome} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        if (arg.startsWith("minecraft:")) {
            arg = arg.substring(10);
        }
        try {
            return Biome.valueOf(arg.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
