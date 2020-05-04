package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashSet;
import java.util.Set;

/**
 * Default tab completer for {@link EquipmentSlot} type.
 */
@Managed
public class EquipmentSlotTabCompleter implements TabCompleter {
    /**
     * Get suggestions based on partial equipment slot name.
     * @param partial partial parameter value
     * @return set of equipment slot names matching given partial value
     */
    @Override
    public Set<String> getSuggestions(String partial) {
        Set<String> result = new HashSet<>();
        partial = partial.toLowerCase();
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.name().toLowerCase().startsWith(partial)) {
                result.add(equipmentSlot.name().toLowerCase());
            }
        }
        return result;
    }
}
