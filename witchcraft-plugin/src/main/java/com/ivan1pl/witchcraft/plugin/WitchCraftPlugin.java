package com.ivan1pl.witchcraft.plugin;

import com.ivan1pl.witchcraft.commands.base.AnnotationBasedCommandExecutor;
import com.ivan1pl.witchcraft.commands.exceptions.CommandAlreadyExistsException;
import com.ivan1pl.witchcraft.commands.exceptions.CommandDefinitionNotFoundException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

/**
 * Base plugin class, extend it to create your plugin.
 */
public class WitchCraftPlugin extends JavaPlugin {
    private AnnotationBasedCommandExecutor annotationBasedCommandExecutor;

    /**
     * Executed when the plugin is enabled. Override to add additional behaviour (remember to invoke the method from the
     * superclass if you do, otherwise annotations will not be processed).
     */
    @Override
    public void onEnable() {
        super.onEnable();
        try {
            annotationBasedCommandExecutor = new AnnotationBasedCommandExecutor(this);
        } catch (CommandAlreadyExistsException | InvocationTargetException | NoSuchMethodException |
                InstantiationException | IllegalAccessException | CommandDefinitionNotFoundException e) {
            getLogger().severe("Failed to initialize command executor, the plugin will not be enabled\n" +
                    ExceptionUtils.getFullStackTrace(e));
            setEnabled(false);
        }
    }

    /**
     * Executed when the plugin is disabled. Override to add additional behaviour (remember to invoke the method from
     * the superclass if you do).
     */
    @Override
    public void onDisable() {
        super.onDisable();
        if (annotationBasedCommandExecutor != null) {
            annotationBasedCommandExecutor.disable();
            annotationBasedCommandExecutor = null;
        }
    }
}
