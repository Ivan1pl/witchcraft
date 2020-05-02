package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.entity.Player;

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
     * @param playerAdapter player adapter
     */
    public DefaultAdapters(BooleanAdapter booleanAdapter,
                           IntegerAdapter integerAdapter,
                           LongAdapter longAdapter,
                           FloatAdapter floatAdapter,
                           DoubleAdapter doubleAdapter,
                           PlayerAdapter playerAdapter) {
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
        adapters.put(Player.class, playerAdapter);
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
