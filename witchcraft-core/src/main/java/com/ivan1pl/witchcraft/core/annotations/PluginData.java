package com.ivan1pl.witchcraft.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for {@code plugin.yml} file generation.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface PluginData {
    /**
     * Plugin name.
     */
    String name();

    /**
     * Plugin version.
     */
    String version();

    /**
     * Plugin description.
     */
    String description() default "";

    /**
     * Spigot API version.
     */
    String apiVersion() default "";

    /**
     * Server load stage when the plugin should be loaded.
     */
    LoadStage loadStage() default LoadStage.POSTWORLD;

    /**
     * Plugin authors.
     */
    String[] authors() default {};

    /**
     * The plugin's or author's website.
     */
    String website() default "";

    /**
     * A list of plugins that this plugin requires to load.
     */
    String[] depend() default {};

    /**
     * The name to use when logging to console instead of the plugin's name.
     */
    String prefix() default "";

    /**
     * A list of plugins that are required for this plugin to have full functionality.
     */
    String[] softDepend() default {};

    /**
     * A list of plugins that should be loaded after this plugin.
     */
    String[] loadBefore() default {};

    /**
     * Permissions supported by this plugin.
     */
    Permission[] permissions() default {};
}
