package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.EntityEffect;

/**
 * Default type adapter for {@link EntityEffect} type.
 */
@Managed
public class EntityEffectAdapter implements TypeAdapter {
    /**
     * Convert string to {@link EntityEffect}.
     * @param arg value to convert
     * @return {@link EntityEffect} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        arg = arg.toLowerCase();
        try {
            return EntityEffect.valueOf(arg.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
