package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.WeatherType;

/**
 * Default type adapter for {@link WeatherType} type.
 */
@Managed
public class WeatherTypeAdapter implements TypeAdapter {
    /**
     * Convert string to {@link WeatherType}.
     * @param arg value to convert
     * @return {@link WeatherType} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        arg = arg.toLowerCase();
        try {
            return WeatherType.valueOf(arg.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
