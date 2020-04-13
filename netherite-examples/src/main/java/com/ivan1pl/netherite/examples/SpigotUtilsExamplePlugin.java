package com.ivan1pl.netherite.examples;

import com.ivan1pl.netherite.commands.base.NetheritePlugin;
import com.ivan1pl.netherite.core.annotations.ChildPermission;
import com.ivan1pl.netherite.core.annotations.Permission;
import com.ivan1pl.netherite.core.annotations.PermissionDefault;
import com.ivan1pl.netherite.core.annotations.PluginData;

/**
 * Base plugin class.
 */
@PluginData(
        name = "netherite-example",
        version = "0.1.0",
        description = "Example plugin built with netherite",
        apiVersion = "1.13",
        authors = "Ivan1pl",
        permissions = {
                @Permission(
                        node = "netherite-example-plugin.netherite-test.*",
                        description = "Default permission",
                        defaultValue = PermissionDefault.TRUE,
                        children = {
                                @ChildPermission(
                                        node = "netherite-example-plugin.netherite-test.any",
                                        inherit = false),
                                @ChildPermission(
                                        node = "netherite-example-plugin.netherite-test.player",
                                        inherit = false),
                                @ChildPermission(
                                        node = "netherite-example-plugin.netherite-test.console",
                                        inherit = false)
                        }),
                @Permission(
                        node = "netherite-example-plugin.netherite-test.any",
                        description = "Permission to execute /netherite-test any-sender",
                        defaultValue = PermissionDefault.TRUE),
                @Permission(
                        node = "netherite-example-plugin.netherite-test.player",
                        description = "Permission to execute /netherite-test player-sender",
                        defaultValue = PermissionDefault.TRUE),
                @Permission(
                        node = "netherite-example-plugin.netherite-test.console",
                        description = "Permission to execute /netherite-test console-sender",
                        defaultValue = PermissionDefault.TRUE)
        })
public class SpigotUtilsExamplePlugin extends NetheritePlugin {
}
