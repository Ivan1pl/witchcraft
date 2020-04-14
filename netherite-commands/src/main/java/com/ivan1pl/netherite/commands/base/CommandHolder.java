package com.ivan1pl.netherite.commands.base;

import com.ivan1pl.netherite.commands.annotations.*;
import com.ivan1pl.netherite.commands.exceptions.CommandAlreadyExistsException;
import com.ivan1pl.netherite.core.builders.MessageBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class holding the instance of a class annotated with {@link Command}
 * annotation and a mapping from subcommand names to methods.
 */
class CommandHolder {
    private final NetheritePlugin netheritePlugin;
    private final String commandName;
    private final String commandDescription;
    private final Object commandObject;
    private final Map<String, Method> subcommands = new HashMap<>();

    /**
     * Create new instance.
     * @param netheritePlugin plugin instance
     * @param commandName command name
     * @param commandDescription command description
     * @param commandClass command class
     */
    CommandHolder(NetheritePlugin netheritePlugin, String commandName, String commandDescription, Class<?> commandClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException,
            CommandAlreadyExistsException {
        this.netheritePlugin = netheritePlugin;
        this.commandName = commandName;
        this.commandDescription = commandDescription;
        this.commandObject = commandClass.getConstructor().newInstance();
        initSubcommands();
    }

    /**
     * Scan command class for methods annotated with {@link SubCommand} annotation.
     * @throws CommandAlreadyExistsException when there are two subcommands with the same name
     */
    private void initSubcommands() throws CommandAlreadyExistsException {
        for (Method m : commandObject.getClass().getMethods()) {
            SubCommand subCommand = m.getAnnotation(SubCommand.class);
            if (subCommand != null) {
                netheritePlugin.getLogger().info(String.format("Processing subcommand: %s", subCommand.value()));
                Method method = subcommands.get(subCommand.value());
                if (method != null) {
                    throw new CommandAlreadyExistsException(String.format(
                            "Subcommand \"%s\" already exists within command %s", subCommand.value(), commandName));
                }
                subcommands.put(subCommand.value(), m);
                netheritePlugin.getLogger().info(String.format("Registered subcommand: %s", subCommand.value()));
            }
        }
        if (!subcommands.containsKey("help")) {
            subcommands.put("help", null);
        }
    }

    /**
     * Try to execute a command.
     * @param commandSender command sender
     * @param args command arguments
     * @return {@code true} if matching method found, {@code false} otherwise
     */
    boolean match(CommandSender commandSender, String[] args) {
        String first = args.length == 0 ? null : args[0];
        ExecutionStatus executionStatus = null;
        Command command =
                commandObject.getClass().getAnnotation(Command.class);
        if (command != null && !command.permission().isEmpty()) {
            if (!commandSender.hasPermission(command.permission())) {
                executionStatus = ExecutionStatus.INSUFFICIENT_PERMISSION;
            }
        }
        if (executionStatus == null) {
            for (String subCommandName : subcommands.keySet()) {
                int argsIndex = subCommandName.isEmpty() ? 0 : 1;
                if (subCommandName.equals(first) || subCommandName.isEmpty()) {
                    if ("help".equalsIgnoreCase(first) && subcommands.get("help") == null) {
                        Help.help(commandSender, commandName, commandDescription, args.length > 1 ? args[1] : null,
                                subcommands);
                        return true;
                    } else {
                        executionStatus = ExecutionStatus.max(executionStatus,
                                match(subcommands.get(subCommandName), commandSender, args, argsIndex));
                        if (executionStatus == ExecutionStatus.ERROR) {
                            commandSender.sendMessage(new MessageBuilder()
                                    .color(ChatColor.DARK_RED)
                                    .append("Internal server error")
                                    .resetColor()
                                    .build());
                        }
                        if (executionStatus == ExecutionStatus.SUCCESS ||
                                executionStatus == ExecutionStatus.ERROR) {
                            return true;
                        }
                    }
                }
            }
        }
        if (executionStatus == ExecutionStatus.CANNOT_EXECUTE) {
            commandSender.sendMessage(new MessageBuilder()
                    .color(ChatColor.RED)
                    .append("You are not allowed to execute this command")
                    .resetColor()
                    .build());
            return true;
        }
        if (executionStatus == ExecutionStatus.INSUFFICIENT_PERMISSION) {
            commandSender.sendMessage(new MessageBuilder()
                    .color(ChatColor.RED)
                    .append("Insufficient permission")
                    .resetColor()
                    .build());
            return true;
        }
        Help.help(commandSender, commandName, commandDescription, null, subcommands);
        return true;
    }

    /**
     * Try to execute specific method with given arguments.
     * @param m method to execute
     * @param commandSender command sender
     * @param args command arguments
     * @param argsIndex index of the first parameter
     * @return {@code true} if arguments match with method parameters, {@code false} otherwise
     */
    private ExecutionStatus match(Method m, CommandSender commandSender, String[] args, int argsIndex) {
        Object[] params = new Object[m.getParameterCount()];
        Parameter[] methodParameters = m.getParameters();
        SubCommand subCommand = m.getAnnotation(SubCommand.class);
        if (subCommand != null && !subCommand.permission().isEmpty()) {
            if (!commandSender.hasPermission(subCommand.permission())) {
                return ExecutionStatus.CANNOT_EXECUTE;
            }
        }
        for (int i = 0; i < methodParameters.length; ++i) {
            Parameter parameter = methodParameters[i];

            if (parameter.getAnnotation(Sender.class) != null) {
                if (parameter.getType().isAssignableFrom(commandSender.getClass())) {
                    params[i] = commandSender;
                } else {
                    return ExecutionStatus.CANNOT_EXECUTE;
                }
            } else {
                ConfigurationValue configurationValue = parameter.getAnnotation(ConfigurationValue.class);
                if (configurationValue != null) {
                    params[i] = netheritePlugin.getConfig().getObject(
                            configurationValue.value(), parameter.getType(), null);
                } else {
                    String value;
                    if (argsIndex < args.length) {
                        value = args[argsIndex];
                    } else {
                        Optional optional = parameter.getAnnotation(Optional.class);
                        if (optional != null) {
                            value = optional.value();
                        } else {
                            return ExecutionStatus.FAILURE;
                        }
                    }
                    Object valueToSet = assignValue(
                            value, parameter.getType(), parameter.getAnnotation(Adapter.class));
                    if (valueToSet == null) {
                        return ExecutionStatus.FAILURE;
                    } else {
                        params[i] = valueToSet;
                    }
                    argsIndex++;
                }
            }
        }
        try {
            m.invoke(commandObject, params);
            return ExecutionStatus.SUCCESS;
        } catch (IllegalAccessException | InvocationTargetException e) {
            netheritePlugin.getLogger().severe(
                    "Failed to execute subcommand method\n" + ExceptionUtils.getFullStackTrace(e));
            return ExecutionStatus.FAILURE;
        } catch (Exception e) {
            netheritePlugin.getLogger().severe("An exception occured while executing subcommand method\n" +
                    ExceptionUtils.getFullStackTrace(e));
            return ExecutionStatus.ERROR;
        }
    }

    /**
     * Get parameter value from string.
     * @param stringValue string value
     * @param expectedType expected type
     * @param adapter type adapter annotation
     * @return computed parameter value
     */
    private Object assignValue(String stringValue, Class<?> expectedType, Adapter adapter) {
        if (expectedType.isAssignableFrom(stringValue.getClass())) {
            return stringValue;
        } else if (adapter != null) {
            TypeAdapter typeAdapter;
            try {
                typeAdapter = adapter.value().getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                    NoSuchMethodException e) {
                netheritePlugin.getLogger().severe(
                        "Unable to instantiate type adapter: no default constructor found or it is inaccessible\n"
                                + ExceptionUtils.getFullStackTrace(e));
                return null;
            }
            Object value = typeAdapter.convert(stringValue);
            if (value != null && expectedType.isAssignableFrom(value.getClass())) {
                return value;
            } else {
                return null;
            }
        } else {
            if (expectedType == boolean.class || expectedType == Boolean.class) {
                return "1".equals(stringValue) || "t".equalsIgnoreCase(stringValue) ||
                        "true".equalsIgnoreCase(stringValue);
            }
            if (expectedType == int.class || expectedType == Integer.class) {
                try {
                    return Integer.parseInt(stringValue);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            if (expectedType == long.class || expectedType == Long.class) {
                try {
                    return Long.parseLong(stringValue);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            if (expectedType == float.class || expectedType == Float.class) {
                try {
                    return Float.parseFloat(stringValue);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            if (expectedType == double.class || expectedType == Double.class) {
                try {
                    return Double.parseDouble(stringValue);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            if (expectedType.isAssignableFrom(Player.class)) {
                Player player = netheritePlugin.getServer().getPlayer(stringValue);
                if (player != null) {
                    return player;
                } else {
                    OfflinePlayer[] players = netheritePlugin.getServer().getOfflinePlayers();
                    for (OfflinePlayer offlinePlayer : players) {
                        if (stringValue.equalsIgnoreCase(offlinePlayer.getName())) {
                            return offlinePlayer.getPlayer();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get tab completions for current argument.
     * @param commandSender command sender
     * @param args command arguments
     * @return set of tab complete suggestions
     */
    Set<String> getTabCompletions(CommandSender commandSender, String[] args) {
        String first = args.length == 0 ? null : args[0];
        Set<String> suggestions = new HashSet<>();
        for (String subCommandName : subcommands.keySet()) {
            int argsIndex = subCommandName.isEmpty() ? 0 : 1;
            if (subCommandName.equals(first) && args.length > 1 || subCommandName.isEmpty()) {
                if ("help".equalsIgnoreCase(first) && subcommands.get("help") == null && args.length == 2) {
                    for (Map.Entry<String, Method> methodEntry : subcommands.entrySet()) {
                        if (methodEntry.getKey().startsWith(args[1])) {
                            Method m = methodEntry.getValue();
                            SubCommand subCommand = m == null ? null : m.getAnnotation(SubCommand.class);
                            if (subCommand == null || subCommand.permission().isEmpty() ||
                                    commandSender.hasPermission(subCommand.permission())) {
                                suggestions.add(methodEntry.getKey());
                            }
                        }
                    }
                } else {
                    suggestions.addAll(
                            getTabCompletions(commandSender, subcommands.get(subCommandName), args, argsIndex));
                }
            } else if (args.length == 1 && subCommandName.toLowerCase().startsWith(args[0].toLowerCase())) {
                Method m = subcommands.get(subCommandName);
                if ("help".equals(subCommandName) && m == null) {
                    suggestions.add("help");
                } else {
                    SubCommand subCommand = m.getAnnotation(SubCommand.class);
                    if (subCommand == null || subCommand.permission().isEmpty() ||
                            commandSender.hasPermission(subCommand.permission())) {
                        suggestions.add(subCommandName);
                    }
                }
            }
        }
        return suggestions;
    }

    /**
     * Get tab completions for a method with given arguments.
     * @param commandSender command sender
     * @param m method to check
     * @param args command arguments
     * @param argsIndex index of the first parameter
     * @return set of tab complete suggestions
     */
    private Set<String> getTabCompletions(CommandSender commandSender, Method m, String[] args, int argsIndex) {
        SubCommand subCommand = m.getAnnotation(SubCommand.class);
        if (subCommand != null && !subCommand.permission().isEmpty()) {
            if (!commandSender.hasPermission(subCommand.permission())) {
                return new HashSet<>();
            }
        }
        Parameter[] methodParameters = m.getParameters();
        for (Parameter parameter : methodParameters) {
            if (parameter.getAnnotation(Sender.class) == null &&
                    parameter.getAnnotation(ConfigurationValue.class) == null) {
                if (argsIndex >= args.length) {
                    return new HashSet<>();
                }
                if (argsIndex == args.length - 1) {
                    return getTabCompletions(args[argsIndex], parameter.getType(),
                            parameter.getAnnotation(TabComplete.class));
                }
                argsIndex++;
            }
        }
        return new HashSet<>();
    }

    /**
     * Get tab completions for given partial value.
     * @param partial partial value
     * @param expectedType expected type
     * @param tabComplete tab complete annotation
     * @return set of tab complete suggestions
     */
    private Set<String> getTabCompletions(String partial, Class<?> expectedType, TabComplete tabComplete) {
        if (expectedType.isAssignableFrom(Player.class)) {
            return netheritePlugin.getServer().matchPlayer(partial).stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toSet());
        } else if (tabComplete != null) {
            try {
                TabCompleter tabCompleter = tabComplete.value().getConstructor().newInstance();
                return tabCompleter.getSuggestions(partial);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                netheritePlugin.getLogger().severe(
                        "Failed to instantiate tab completer: no default constructor found or it is inaccessible\n"
                                + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return new HashSet<>();
    }

    /**
     * Command execution status.
     */
    private enum ExecutionStatus {
        /**
         * Failed to match any command.
         */
        FAILURE(0),

        /**
         * Insufficient permission to execute the command.
         */
        INSUFFICIENT_PERMISSION(1),

        /**
         * Not allowed to execute the command (for example when executing from console and the command is expecting
         * a player).
         */
        CANNOT_EXECUTE(2),

        /**
         * Successfully matched and started but there was an exception during execution.
         */
        ERROR(3),

        /**
         * Successfully executed.
         */
        SUCCESS(4);

        private int statusCode;

        ExecutionStatus(int statusCode) {
            this.statusCode = statusCode;
        }

        private static ExecutionStatus max(ExecutionStatus e1, ExecutionStatus e2) {
            if (e1 == null) return e2;
            if (e2 == null) return e1;
            return e1.statusCode > e2.statusCode ? e1 : e2;
        }
    }
}
