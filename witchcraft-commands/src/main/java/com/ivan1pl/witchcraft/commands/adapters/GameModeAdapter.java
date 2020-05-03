package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.GameMode;

/**
 * Default type adapter for {@link GameMode} type.
 */
@Managed
public class GameModeAdapter implements TypeAdapter {
    /**
     * Convert string to {@link GameMode}.
     * @param arg value to convert
     * @return {@link GameMode} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        arg = arg.toLowerCase();
        try {
            return GameMode.valueOf(arg.toUpperCase());
        } catch (Exception e) {
            try {
                return GameMode.getByValue(Integer.parseInt(arg));
            } catch (Exception e1) {
                return null;
            }
        }
    }
}
