package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping from class to adapter responsible for converting to that class.
 */
@Managed
public class DefaultAdapters {
    private final Map<Class<?>, TypeAdapter> adapters = new HashMap<>();

    /**
     * Create adapters mapping.
     * @param booleanAdapter boolean adapter
     * @param integerAdapter integer adapter
     * @param longAdapter long adapter
     * @param floatAdapter float adapter
     * @param doubleAdapter double adapter
     * @param biomeAdapter  biome adapter
     * @param entityEffectAdapter entity effect adapter
     * @param entityTypeAdapter entity type adapter
     * @param equipmentSlotAdapter equipment slot adapter
     * @param gameModeAdapter game mode adapter
     * @param instrumentAdapter instrument adapter
     * @param materialAdapter material adapter
     * @param playerAdapter player adapter
     * @param potionTypeAdapter potion type adapter
     * @param weatherTypeAdapter weather type adapter
     * @param worldAdapter world adapter
     * @param worldTypeAdapter world type adapter
     */
    public DefaultAdapters(BooleanAdapter booleanAdapter,
                           IntegerAdapter integerAdapter,
                           LongAdapter longAdapter,
                           FloatAdapter floatAdapter,
                           DoubleAdapter doubleAdapter,
                           BiomeAdapter biomeAdapter,
                           EntityEffectAdapter entityEffectAdapter,
                           EntityTypeAdapter entityTypeAdapter,
                           EquipmentSlotAdapter equipmentSlotAdapter,
                           GameModeAdapter gameModeAdapter,
                           InstrumentAdapter instrumentAdapter,
                           MaterialAdapter materialAdapter,
                           PlayerAdapter playerAdapter,
                           PotionTypeAdapter potionTypeAdapter,
                           WeatherTypeAdapter weatherTypeAdapter,
                           WorldAdapter worldAdapter,
                           WorldTypeAdapter worldTypeAdapter) {
        adapters.put(boolean.class, booleanAdapter);
        adapters.put(Boolean.class, booleanAdapter);
        adapters.put(int.class, integerAdapter);
        adapters.put(Integer.class, integerAdapter);
        adapters.put(long.class, longAdapter);
        adapters.put(Long.class, longAdapter);
        adapters.put(float.class, floatAdapter);
        adapters.put(Float.class, floatAdapter);
        adapters.put(double.class, doubleAdapter);
        adapters.put(Double.class, doubleAdapter);
        //Bukkit classes
        adapters.put(Biome.class, biomeAdapter);
        adapters.put(EntityEffect.class, entityEffectAdapter);
        adapters.put(EntityType.class, entityTypeAdapter);
        adapters.put(EquipmentSlot.class, equipmentSlotAdapter);
        adapters.put(GameMode.class, gameModeAdapter);
        adapters.put(Instrument.class, instrumentAdapter);
        adapters.put(Material.class, materialAdapter);
        adapters.put(Player.class, playerAdapter);
        adapters.put(PotionType.class, potionTypeAdapter);
        adapters.put(WeatherType.class, weatherTypeAdapter);
        adapters.put(World.class, worldAdapter);
        adapters.put(WorldType.class, worldTypeAdapter);
    }

    /**
     * Get type adapter for given type.
     * @param requestedType requested type
     * @return adapter associated with requested type or {@code null}
     */
    public TypeAdapter get(Class<?> requestedType) {
        return adapters.get(requestedType);
    }
}
