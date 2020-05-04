package com.ivan1pl.witchcraft.commands.completers;

import com.ivan1pl.witchcraft.commands.base.TabCompleter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping from class to tab completer responsible for completing parameters of that class.
 */
@Managed
public class DefaultCompleters {
    private final Map<Class<?>, TabCompleter> completers = new HashMap<>();

    /**
     * Create completers mapping.
     * @param biomeTabCompleter biome tab completer
     * @param blockDataTabCompleter block data tab completer
     * @param entityEffectTabCompleter entity effect tab completer
     * @param entityTypeTabCompleter entity type tab completer
     * @param equipmentSlotTabCompleter equipment slot tab completer
     * @param gameModeTabCompleter game mode tab completer
     * @param instrumentTabCompleter instrument tab completer
     * @param materialTabCompleter material tab completer
     * @param playerTabCompleter player tab completer
     * @param potionTypeTabCompleter potion type tab completer
     * @param weatherTypeTabCompleter weather type tab completer
     * @param worldTabCompleter world tab completer
     * @param worldTypeTabCompleter world type tab completer
     */
    public DefaultCompleters(BiomeTabCompleter biomeTabCompleter,
                             BlockDataTabCompleter blockDataTabCompleter,
                             EntityEffectTabCompleter entityEffectTabCompleter,
                             EntityTypeTabCompleter entityTypeTabCompleter,
                             EquipmentSlotTabCompleter equipmentSlotTabCompleter,
                             GameModeTabCompleter gameModeTabCompleter,
                             InstrumentTabCompleter instrumentTabCompleter,
                             MaterialTabCompleter materialTabCompleter,
                             PlayerTabCompleter playerTabCompleter,
                             PotionTypeTabCompleter potionTypeTabCompleter,
                             WeatherTypeTabCompleter weatherTypeTabCompleter,
                             WorldTabCompleter worldTabCompleter,
                             WorldTypeTabCompleter worldTypeTabCompleter) {
        completers.put(Biome.class, biomeTabCompleter);
        completers.put(BlockData.class, blockDataTabCompleter);
        completers.put(EntityEffect.class, entityEffectTabCompleter);
        completers.put(EntityType.class, entityTypeTabCompleter);
        completers.put(EquipmentSlot.class, equipmentSlotTabCompleter);
        completers.put(GameMode.class, gameModeTabCompleter);
        completers.put(Instrument.class, instrumentTabCompleter);
        completers.put(Material.class, materialTabCompleter);
        completers.put(Player.class, playerTabCompleter);
        completers.put(PotionType.class, potionTypeTabCompleter);
        completers.put(WeatherType.class, weatherTypeTabCompleter);
        completers.put(World.class, worldTabCompleter);
        completers.put(WorldType.class, worldTypeTabCompleter);
    }

    /**
     * Get tab completer for given type.
     * @param requestedType requested type
     * @return tab completer associated with requested type or {@code null}
     */
    public TabCompleter get(Class<?> requestedType) {
        return completers.get(requestedType);
    }
}
