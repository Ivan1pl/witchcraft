package com.ivan1pl.witchcraft.plugin;

import com.ivan1pl.witchcraft.commands.annotations.Command;
import com.ivan1pl.witchcraft.commands.base.AnnotationBasedCommandExecutor;
import com.ivan1pl.witchcraft.commands.exceptions.CommandAlreadyExistsException;
import com.ivan1pl.witchcraft.commands.exceptions.CommandDefinitionNotFoundException;
import com.ivan1pl.witchcraft.context.WitchCraftContext;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import com.ivan1pl.witchcraft.context.annotations.Plugin;
import com.ivan1pl.witchcraft.context.exception.CandidateNotFoundException;
import com.ivan1pl.witchcraft.context.exception.InitializationFailedException;
import com.ivan1pl.witchcraft.context.exception.NonUniqueCandidateException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

/**
 * Base plugin class, extend it to create your plugin.
 */
public class WitchCraftPlugin extends JavaPlugin {
    private AnnotationBasedCommandExecutor annotationBasedCommandExecutor;
    private WitchCraftContext witchCraftContext;

    /**
     * Executed when the plugin is enabled. This method is marked as final, if you want to add your own logic to it
     * then override {@link #preInit} or {@link #postInit}. This method executes them in order: {@link #preInit},
     * {@link #init()}, {@link #postInit}.
     */
    @Override
    public final void onEnable() {
        super.onEnable();
        preInit();
        init();
        postInit();
    }

    /**
     * Initialize dependency injection context and command executor.
     */
    private void init() {
        try {
            witchCraftContext = createWitchCraftContext(this);
            witchCraftContext.init();
            annotationBasedCommandExecutor = new AnnotationBasedCommandExecutor(this, witchCraftContext);
        } catch (CommandAlreadyExistsException | CommandDefinitionNotFoundException | CandidateNotFoundException |
                NonUniqueCandidateException e) {
            getLogger().severe("Failed to initialize command executor, the plugin will not be enabled\n" +
                    ExceptionUtils.getFullStackTrace(e));
            setEnabled(false);
        } catch (InitializationFailedException e) {
            getLogger().severe("Failed to initialize dependency injection context, the plugin will not be enabled\n"
                    + ExceptionUtils.getFullStackTrace(e));
            setEnabled(false);
        }
    }

    /**
     * Create dependency injection context for given plugin instance.
     * @param pluginInstance plugin instance
     * @return dependency injection context (not initialized)
     */
    private WitchCraftContext createWitchCraftContext(JavaPlugin pluginInstance) {
        String basePackage = pluginInstance.getClass().getPackage() == null ?
                null : pluginInstance.getClass().getPackage().getName();
        Plugin plugin = pluginInstance.getClass().getAnnotation(Plugin.class);
        if (plugin != null && !plugin.basePackage().isEmpty()) {
            basePackage = plugin.basePackage();
        }
        return new WitchCraftContext(pluginInstance, basePackage, Managed.class, Command.class);
    }

    /**
     * Extend this method to add your own logic to plugin enable phase. This method executes before the dependency
     * injection context and command executor are initialized. This is a good place to save default config (if present).
     */
    protected void preInit() {}

    /**
     * Extend this method to add your own logic to plugin enable phase. This method executes after the dependency
     * injection context and command executor are initialized.
     */
    protected void postInit() {}

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
        if (witchCraftContext != null) {
            witchCraftContext.clear();
            witchCraftContext = null;
        }
    }

    /**
     * Get dependency injection context.
     * @return dependency injection context
     */
    public final WitchCraftContext getWitchCraftContext() {
        return witchCraftContext;
    }
}
