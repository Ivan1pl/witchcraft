package com.ivan1pl.witchcraft.commands.adapters;

import com.ivan1pl.witchcraft.commands.base.TypeAdapter;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Default type adapter for {@link org.bukkit.block.data.BlockData} type.
 */
@Managed
public class BlockDataAdapter implements TypeAdapter {
    private final JavaPlugin javaPlugin;

    public BlockDataAdapter(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    /**
     * Convert string to {@link org.bukkit.block.data.BlockData}.
     * @param arg value to convert
     * @return {@link org.bukkit.block.data.BlockData} representation of given argument
     */
    @Override
    public Object convert(String arg) {
        try {
            return javaPlugin.getServer().createBlockData(arg);
        } catch (Exception e) {
            return null;
        }
    }
}
