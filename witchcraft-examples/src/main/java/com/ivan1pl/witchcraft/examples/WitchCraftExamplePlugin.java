package com.ivan1pl.witchcraft.examples;

import com.ivan1pl.witchcraft.core.annotations.ChildPermission;
import com.ivan1pl.witchcraft.core.annotations.Permission;
import com.ivan1pl.witchcraft.core.annotations.PermissionDefault;
import com.ivan1pl.witchcraft.core.annotations.PluginData;
import com.ivan1pl.witchcraft.plugin.WitchCraftPlugin;

/**
 * Base plugin class.
 */
@PluginData(
        name = "witchcraft-example",
        version = "0.4.0",
        description = "Example plugin built with WitchCraft Framework",
        apiVersion = "1.13",
        authors = "Ivan1pl",
        permissions = {
                @Permission(
                        node = "witchcraft-example-plugin.witchcraft-test.*",
                        description = "Default permission",
                        defaultValue = PermissionDefault.TRUE,
                        children = {
                                @ChildPermission(
                                        node = "witchcraft-example-plugin.witchcraft-test.any",
                                        inherit = false),
                                @ChildPermission(
                                        node = "witchcraft-example-plugin.witchcraft-test.player",
                                        inherit = false),
                                @ChildPermission(
                                        node = "witchcraft-example-plugin.witchcraft-test.console",
                                        inherit = false)
                        }),
                @Permission(
                        node = "witchcraft-example-plugin.witchcraft-test.any",
                        description = "Permission to execute /witchcraft-test any-sender",
                        defaultValue = PermissionDefault.TRUE),
                @Permission(
                        node = "witchcraft-example-plugin.witchcraft-test.player",
                        description = "Permission to execute /witchcraft-test player-sender",
                        defaultValue = PermissionDefault.TRUE),
                @Permission(
                        node = "witchcraft-example-plugin.witchcraft-test.console",
                        description = "Permission to execute /witchcraft-test console-sender",
                        defaultValue = PermissionDefault.TRUE)
        })
public class WitchCraftExamplePlugin extends WitchCraftPlugin {
}
