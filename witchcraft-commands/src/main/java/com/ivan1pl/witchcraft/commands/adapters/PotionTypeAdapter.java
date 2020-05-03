package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.potion.PotionType;

/**
 * Default type adapter for {@link PotionType} type.
 */
@Managed
public class PotionTypeAdapter implements TypeAdapter {
    /**
     * Convert string to {@link PotionType}.
     * @param arg value to convert
     * @return {@link PotionType} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        arg = arg.toLowerCase();
        try {
            return PotionType.valueOf(arg.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
