package com.ivan1pl.netherite.examples;

import com.ivan1pl.spigot.utils.commands.base.BasePlugin;
import com.ivan1pl.spigot.utils.core.annotations.ChildPermission;
import com.ivan1pl.spigot.utils.core.annotations.Permission;
import com.ivan1pl.spigot.utils.core.annotations.PermissionDefault;
import com.ivan1pl.spigot.utils.core.annotations.PluginData;

/**
 * Base plugin class.
 */
@PluginData(
        name = "spigot-utils-example",
        version = "0.1.0",
        description = "Example plugin built with Ivan1pl's spigot-utils",
        apiVersion = "1.13",
        authors = "Ivan1pl",
        permissions = {
                @Permission(
                        node = "spigot-utils-example-plugin.spigot-utils-test.*",
                        description = "Default permission",
                        defaultValue = PermissionDefault.TRUE,
                        children = {
                                @ChildPermission(
                                        node = "spigot-utils-example-plugin.spigot-utils-test.any",
                                        inherit = false),
                                @ChildPermission(
                                        node = "spigot-utils-example-plugin.spigot-utils-test.player",
                                        inherit = false),
                                @ChildPermission(
                                        node = "spigot-utils-example-plugin.spigot-utils-test.console",
                                        inherit = false)
                        }),
                @Permission(
                        node = "spigot-utils-example-plugin.spigot-utils-test.any",
                        description = "Permission to execute /spigot-utils-test any-sender",
                        defaultValue = PermissionDefault.TRUE),
                @Permission(
                        node = "spigot-utils-example-plugin.spigot-utils-test.player",
                        description = "Permission to execute /spigot-utils-test player-sender",
                        defaultValue = PermissionDefault.TRUE),
                @Permission(
                        node = "spigot-utils-example-plugin.spigot-utils-test.console",
                        description = "Permission to execute /spigot-utils-test console-sender",
                        defaultValue = PermissionDefault.TRUE)
        })
public class SpigotUtilsExamplePlugin extends BasePlugin {
}
