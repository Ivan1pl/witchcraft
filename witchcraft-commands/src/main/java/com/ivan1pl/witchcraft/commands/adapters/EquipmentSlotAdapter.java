package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Default type adapter for {@link EquipmentSlot} type.
 */
@Managed
public class EquipmentSlotAdapter implements TypeAdapter {
    /**
     * Convert string to {@link EquipmentSlot}.
     * @param arg value to convert
     * @return {@link EquipmentSlot} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        arg = arg.toLowerCase();
        try {
            return EquipmentSlot.valueOf(arg.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
