package com.ivan1pl.witchcraft.commands.base;

import com.google.common.primitives.Primitives;
import com.ivan1pl.witchcraft.commands.adapters.DefaultAdapters;
import com.ivan1pl.witchcraft.commands.annotations.*;
import com.ivan1pl.witchcraft.commands.annotations.Optional;
import com.ivan1pl.witchcraft.commands.completers.DefaultCompleters;
import com.ivan1pl.witchcraft.commands.exceptions.CommandAlreadyExistsException;
import com.ivan1pl.witchcraft.context.WitchCraftContext;
import com.ivan1pl.witchcraft.context.annotations.ConfigurationValue;
import com.ivan1pl.witchcraft.core.builders.MessageBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class holding the instance of a class annotated with {@link Command}
 * annotation and a mapping from subcommand names to methods.
 */
class CommandHolder {
    private final JavaPlugin javaPlugin;
    private final String commandName;
    private final String commandDescription;
    private final Object commandObject;
    private final DefaultAdapters defaultAdapters;
    private final DefaultCompleters defaultCompleters;
    private final WitchCraftContext witchCraftContext;
    private final Map<String, MethodHolder> subcommands = new HashMap<>();

    /**
     * Create new instance.
     * @param javaPlugin plugin instance
     * @param witchCraftContext dependency injection context
     * @param commandName command name
     * @param commandDescription command description
     * @param commandClass command class
     * @throws CommandAlreadyExistsException when there are several subcommands with the same name
     * @throws NoSuchMethodException when an object in context is actually a malformed proxy which does not contain a
     *                               method from the superclass; this should never happen
     */
    CommandHolder(JavaPlugin javaPlugin, WitchCraftContext witchCraftContext, String commandName,
                  String commandDescription, Class<?> commandClass)
            throws CommandAlreadyExistsException, NoSuchMethodException {
        this.javaPlugin = javaPlugin;
        this.commandName = commandName;
        this.commandDescription = commandDescription;
        this.commandObject = witchCraftContext.get(commandClass);
        this.defaultAdapters = witchCraftContext.get(DefaultAdapters.class);
        this.defaultCompleters = witchCraftContext.get(DefaultCompleters.class);
        this.witchCraftContext = witchCraftContext;
        initSubcommands(commandClass);
    }

    /**
     * Scan command class for methods annotated with {@link SubCommand} annotation.
     * @param commandClass command class
     * @throws CommandAlreadyExistsException when there are two subcommands with the same name
     * @throws NoSuchMethodException when an object is actually a malformed proxy which does not contain a method from
     *                               the superclass; this should never happen
     */
    private void initSubcommands(Class<?> commandClass) throws CommandAlreadyExistsException, NoSuchMethodException {
        for (Method m : commandClass.getMethods()) {
            SubCommand subCommand = m.getAnnotation(SubCommand.class);
            if (subCommand != null) {
                javaPlugin.getLogger().info(String.format("Processing subcommand: %s", subCommand.value()));
                MethodHolder methodHolder = subcommands.get(subCommand.value());
                if (methodHolder != null) {
                    throw new CommandAlreadyExistsException(String.format(
                            "Subcommand \"%s\" already exists within command %s", subCommand.value(), commandName));
                }
                subcommands.put(subCommand.value(), new MethodHolder(
                        commandObject.getClass().getMethod(m.getName(), m.getParameterTypes()), m));
                javaPlugin.getLogger().info(String.format("Registered subcommand: %s", subCommand.value()));
            }
        }
        if (!subcommands.containsKey("help")) {
            subcommands.put("help", new MethodHolder(null, null));
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
                    if ("help".equalsIgnoreCase(first) && subcommands.get("help").getOriginalMethod() == null) {
                        Help.help(commandSender, commandName, commandDescription, args.length > 1 ? args[1] : null,
                                args.length > 2 ? args[2] : null, subcommands);
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
        Help.help(commandSender, commandName, commandDescription, null, null, subcommands);
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
    private ExecutionStatus match(MethodHolder m, CommandSender commandSender, String[] args, int argsIndex) {
        Object[] params = new Object[m.getOriginalMethod().getParameterCount()];
        Parameter[] methodParameters = m.getOriginalMethod().getParameters();
        SubCommand subCommand = m.getOriginalMethod().getAnnotation(SubCommand.class);
        if (subCommand != null && !subCommand.permission().isEmpty()) {
            if (!commandSender.hasPermission(subCommand.permission())) {
                return ExecutionStatus.CANNOT_EXECUTE;
            }
        }
        OptionValues optionValues = new OptionValues();
        boolean optionsSupported = false;
        for (Parameter parameter : methodParameters) {
            Option option = parameter.getAnnotation(Option.class);
            if (option != null) {
                optionValues.addOption(option, parameter.getType());
                optionsSupported = true;
            }
        }
        if (optionsSupported) {
            while (argsIndex < args.length && args[argsIndex].startsWith("-")) {
                List<OptionValue> optionValueList = new LinkedList<>();
                if (args[argsIndex].startsWith("--")) {
                    String longName = args[argsIndex].substring(2);
                    OptionValue optionValue = optionValues.getOption(longName);
                    if (optionValue != null) {
                        optionValueList.add(optionValue);
                    } else {
                        return ExecutionStatus.FAILURE;
                    }
                } else {
                    String shortNames = args[argsIndex].substring(1);
                    char[] chars = shortNames.toCharArray();
                    for (char c : chars) {
                        OptionValue optionValue = optionValues.getOption(c);
                        if (optionValue != null) {
                            optionValueList.add(optionValue);
                        } else {
                            return ExecutionStatus.FAILURE;
                        }
                    }
                }
                boolean paramsEnabled = optionValueList.size() == 1;
                for (OptionValue optionValue : optionValueList) {
                    if (optionValue.isHasParameter() && !paramsEnabled) {
                        return ExecutionStatus.FAILURE;
                    }
                    if (optionValue.isHasParameter()) {
                        if (++argsIndex >= args.length) {
                            return ExecutionStatus.FAILURE;
                        }
                        optionValue.addValue(args[argsIndex]);
                    } else {
                        optionValue.addValue("true");
                    }
                }
                argsIndex++;
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
            } else if (parameter.getAnnotation(Option.class) != null) {
                params[i] = getOptionValue(parameter.getAnnotation(Option.class), parameter.getType(), optionValues,
                        parameter.getAnnotation(Adapter.class));
            } else {
                ConfigurationValue configurationValue = parameter.getAnnotation(ConfigurationValue.class);
                if (configurationValue != null) {
                    params[i] = javaPlugin.getConfig().getObject(
                            configurationValue.value(), Primitives.wrap(parameter.getType()), null);
                } else if (i == methodParameters.length - 1 && parameter.getType().isArray()) {
                    List<String> values = new LinkedList<>();
                    while (argsIndex < args.length) {
                        values.add(args[argsIndex++]);
                    }
                    Class<?> elementType = parameter.getType().getComponentType();
                    Object array = Array.newInstance(elementType, values.size());
                    int index = 0;
                    for (String value : values) {
                        Object valueToSet = assignValue(
                                value, elementType, parameter.getAnnotation(Adapter.class));
                        if (valueToSet == null) {
                            return ExecutionStatus.FAILURE;
                        } else {
                            Array.set(array, index++, valueToSet);
                        }
                    }
                    params[i] = array;
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
            m.getProxyMethod().invoke(commandObject, params);
            return ExecutionStatus.SUCCESS;
        } catch (IllegalAccessException | InvocationTargetException e) {
            javaPlugin.getLogger().severe(
                    "Failed to execute subcommand method\n" + ExceptionUtils.getFullStackTrace(e));
            return ExecutionStatus.FAILURE;
        } catch (Exception e) {
            javaPlugin.getLogger().severe("An exception occured while executing subcommand method\n" +
                    ExceptionUtils.getFullStackTrace(e));
            return ExecutionStatus.ERROR;
        }
    }

    /**
     * Get option's value.
     * @param option option definition
     * @param expectedType expected type
     * @param optionValues option values
     * @param adapter type adapter
     * @return option value
     */
    private Object getOptionValue(Option option, Class<?> expectedType, OptionValues optionValues, Adapter adapter) {
        if (expectedType.isArray()) {
            List<Object> elements = new LinkedList<>();
            Object element;
            while ((element = getOptionValue(option, expectedType.getComponentType(), optionValues, adapter)) != null) {
                elements.add(element);
            }
            Object array = Array.newInstance(expectedType.getComponentType(), elements.size());
            int i = 0;
            for (Object object : elements) {
                Array.set(array, i++, object);
            }
            return array;
        } else {
            OptionValue optionValue = optionValues.getOption(option);
            String stringValue = optionValue == null ? null : optionValue.getValue();
            Object retval = stringValue == null ? null : assignValue(stringValue, expectedType, adapter);
            return optionValue != null && !optionValue.isHasParameter() && retval == null ? false : retval;
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
            TypeAdapter typeAdapter = witchCraftContext.get(adapter.value());
            Object value = typeAdapter.convert(stringValue);
            if (value != null && expectedType.isAssignableFrom(value.getClass())) {
                return value;
            } else {
                return null;
            }
        } else {
            TypeAdapter typeAdapter = defaultAdapters.get(expectedType);
            return typeAdapter == null ? null : typeAdapter.convert(stringValue);
        }
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
                    for (Map.Entry<String, MethodHolder> methodEntry : subcommands.entrySet()) {
                        if (methodEntry.getKey().startsWith(args[1]) && !methodEntry.getKey().isEmpty()) {
                            Method m = methodEntry.getValue().getOriginalMethod();
                            SubCommand subCommand = m == null ? null : m.getAnnotation(SubCommand.class);
                            if (subCommand == null || subCommand.permission().isEmpty() ||
                                    commandSender.hasPermission(subCommand.permission())) {
                                suggestions.add(methodEntry.getKey());
                            }
                        }
                    }
                } else {
                    suggestions.addAll(getTabCompletions(commandSender,
                            subcommands.get(subCommandName).getOriginalMethod(), args, argsIndex));
                }
            } else if (args.length == 1 && subCommandName.toLowerCase().startsWith(args[0].toLowerCase())) {
                Method m = subcommands.get(subCommandName).getOriginalMethod();
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
        if (m == null) {
            return new HashSet<>();
        }
        SubCommand subCommand = m.getAnnotation(SubCommand.class);
        if (subCommand != null && !subCommand.permission().isEmpty()) {
            if (!commandSender.hasPermission(subCommand.permission())) {
                return new HashSet<>();
            }
        }
        Parameter[] methodParameters = m.getParameters();
        OptionValues optionValues = new OptionValues();
        boolean processingOptions = false;
        for (Parameter parameter : methodParameters) {
            Option option = parameter.getAnnotation(Option.class);
            if (option != null) {
                optionValues.addOption(option, parameter.getType());
                processingOptions = true;
            }
        }
        if (processingOptions) {
            OptionValue optionValue = null;
            while (argsIndex < args.length && args[argsIndex].startsWith("-")) {
                if (argsIndex != args.length - 1) {
                    if (args[argsIndex].startsWith("--")) {
                        optionValue = optionValues.getOption(args[argsIndex].substring(2));
                    } else if (args[argsIndex].length() == 2) {
                        optionValue = optionValues.getOption(args[argsIndex].toCharArray()[1]);
                    } else {
                        optionValue = null;
                    }
                    if (optionValue != null && optionValue.isHasParameter()) {
                        argsIndex++;
                    } else {
                        optionValue = null;
                    }
                } else {
                    optionValue = null;
                }
                argsIndex++;
            }
            if (argsIndex >= args.length) {
                if (optionValue != null) {
                    for (Parameter parameter : methodParameters) {
                        Option option = parameter.getAnnotation(Option.class);
                        if (option != null && option.shortName() == optionValue.getShortName() &&
                                option.longName().equals(optionValue.getLongName())) {
                            return getTabCompletions(args[args.length - 1],
                                    parameter.getType().isArray() ?
                                            parameter.getType().getComponentType() : parameter.getType(),
                                    parameter.getAnnotation(TabComplete.class));
                        }
                    }
                } else {
                    final String prefix = args[argsIndex - 1];
                    return optionValues.getPossibleKeys().stream()
                            .filter(s -> s.startsWith(prefix))
                            .collect(Collectors.toSet());
                }
            }
        }
        for (Parameter parameter : methodParameters) {
            if (parameter.getAnnotation(Sender.class) == null &&
                    parameter.getAnnotation(ConfigurationValue.class) == null &&
                    parameter.getAnnotation(Option.class) == null) {
                if (parameter == methodParameters[methodParameters.length - 1] && parameter.getType().isArray()) {
                    return getTabCompletions(args[args.length - 1], parameter.getType().getComponentType(),
                            parameter.getAnnotation(TabComplete.class));
                }
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
        TabCompleter tabCompleter = tabComplete == null ?
                defaultCompleters.get(expectedType) : witchCraftContext.get(tabComplete.value());
        return tabCompleter == null ? new HashSet<>() : tabCompleter.getSuggestions(partial);
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
