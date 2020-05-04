package com.ivan1pl.witchcraft.examples.data;

import com.ivan1pl.witchcraft.context.annotations.ConfigurationValue;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import org.bukkit.plugin.java.JavaPlugin;

@Managed
public class Counter {
    private final JavaPlugin javaPlugin;
    private int value;

    public Counter(JavaPlugin javaPlugin, @ConfigurationValue("witchcraft.example.counter.initVal") int initVal) {
        this.javaPlugin = javaPlugin;
        this.value = initVal;
    }

    public void increase() {
        value++;
        javaPlugin.getLogger().info("Increase counter");
    }

    public void decrease() {
        value--;
        javaPlugin.getLogger().info("Decrease counter");
    }

    public int getValue() {
        return value;
    }
}
