package com.ivan1pl.netherite.commands.base;

import com.ivan1pl.netherite.commands.annotations.*;
import com.ivan1pl.spigot.utils.commands.annotations.*;
import com.ivan1pl.netherite.core.builders.MessageBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default help provider.
 */
class Help {
    /**
     * Display default help message.
     * @param commandSender command sender
     * @param commandName command name
     * @param firstArg first argument (possibly a subcommand)
     * @param subcommands subcommands
     */
    static void help(CommandSender commandSender, String commandName, String firstArg,
                             Map<String, Method> subcommands) {
        Map<String, Method> availableSubcommands = new HashMap<>();
        for (Map.Entry<String, Method> methodEntry : subcommands.entrySet()) {
            Method m = methodEntry.getValue();
            if (m == null) {
                availableSubcommands.put(methodEntry.getKey(), m);
            } else {
                SubCommand subCommand = m.getAnnotation(SubCommand.class);
                if (subCommand == null || subCommand.permission().isEmpty() ||
                        commandSender.hasPermission(subCommand.permission())) {
                    availableSubcommands.put(methodEntry.getKey(), m);
                }
            }
        }
        if (firstArg == null) {
            generalHelp(commandSender, commandName, availableSubcommands);
        } else {
            Method subcommandMethod = availableSubcommands.get(firstArg);
            if (subcommandMethod == null) {
                commandSender.sendMessage(new MessageBuilder()
                        .color(ChatColor.RED)
                        .append("No help available for '").append(firstArg).append("'")
                        .resetColor().build());
            } else {
                detailedHelp(commandSender, commandName, firstArg, subcommandMethod);
            }
        }
    }

    /**
     * Display general command description and list of subcommands.
     * @param commandSender command sender
     * @param commandName command name
     * @param subcommands available subcommands
     */
    private static void generalHelp(CommandSender commandSender, String commandName,
                                    Map<String, Method> subcommands) {
        List<Map.Entry<String, Method>> subcommandsSorted = new ArrayList<>(subcommands.entrySet());
        subcommandsSorted.sort(Map.Entry.comparingByKey());
        if (subcommandsSorted.isEmpty()) {
            commandSender.sendMessage(new MessageBuilder()
                    .color(ChatColor.RED).append("No help is available").resetColor().build());
        } else {
            int i = 0;
            if (subcommandsSorted.get(0).getKey().isEmpty()) {
                detailedHelp(commandSender, commandName, null, subcommandsSorted.get(0).getValue());
                i++;
            }
            if (subcommandsSorted.size() > i) {
                MessageBuilder messageBuilder = new MessageBuilder()
                        .color(ChatColor.AQUA).append("Use ")
                        .color(ChatColor.DARK_RED).append(commandName).append(" help <")
                        .color(ChatColor.RED).append("subcommand name").color(ChatColor.DARK_RED).append(">")
                        .color(ChatColor.AQUA).append(" to display detailed help for specific subcommand.")
                        .resetColor().newLine().color(ChatColor.AQUA).append("Available subcommands:")
                        .resetColor().newLine();
                for (; i < subcommandsSorted.size(); ++i) {
                    String subcommandName = subcommandsSorted.get(i).getKey();
                    String description;
                    if (subcommandsSorted.get(i).getValue() == null) {
                        description = "Display help";
                    } else {
                        Description subcommandDescription = subcommandsSorted.get(i).getValue()
                                .getAnnotation(Description.class);
                        description = subcommandDescription == null ?
                                "" : subcommandDescription.shortDescription();
                    }
                    messageBuilder.color(ChatColor.AQUA).append("> ").color(ChatColor.GREEN).append(subcommandName);
                    if (!description.isEmpty()) {
                        messageBuilder.color(ChatColor.AQUA).append(" - ").append(description);
                    }
                    messageBuilder.resetColor().newLine();
                }
                commandSender.sendMessage(messageBuilder.build());
            }
        }
    }

    /**
     * Display detailed description of a specific subcommand.
     * @param commandSender command sender
     * @param commandName command name
     * @param subcommandName subcommand name
     * @param commandMethod subcommand method
     */
    private static void detailedHelp(CommandSender commandSender, String commandName, String subcommandName,
                                     Method commandMethod) {
        MessageBuilder messageBuilder = new MessageBuilder().color(ChatColor.AQUA).append("> ")
                .color(ChatColor.GREEN).append(commandName);
        if (subcommandName != null && !subcommandName.isEmpty()) {
            messageBuilder.append(" ").append(subcommandName);
        }
        for (Parameter parameter : commandMethod.getParameters()) {
            if (parameter.getAnnotation(Sender.class) == null &&
                    parameter.getAnnotation(ConfigurationValue.class) == null) {
                String name = parameter.getName();
                Optional optional = parameter.getAnnotation(Optional.class);
                if (optional == null) {
                    messageBuilder.append(" <").append(name).append(">");
                } else {
                    messageBuilder.append(" [").append(name).append("=").append(optional.value()).append("]");
                }
            }
        }
        Description subcommandDescription = commandMethod.getAnnotation(Description.class);
        String shortDescription = subcommandDescription == null ? "" : subcommandDescription.shortDescription();
        String longDescription = subcommandDescription == null ? "" : subcommandDescription.detailedDescription();
        if (!shortDescription.isEmpty()) {
            messageBuilder.color(ChatColor.AQUA).append(" - ").append(shortDescription);
        }
        messageBuilder.resetColor().newLine();
        if (!longDescription.isEmpty()) {
            messageBuilder.color(ChatColor.AQUA).append(longDescription).resetColor().newLine();
        }
        commandSender.sendMessage(messageBuilder.build());
    }
}
