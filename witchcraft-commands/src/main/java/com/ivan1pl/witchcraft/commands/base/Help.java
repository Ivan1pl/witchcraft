package com.ivan1pl.witchcraft.commands.base;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ivan1pl.witchcraft.commands.annotations.Optional;
import com.ivan1pl.witchcraft.context.annotations.ConfigurationValue;
import com.ivan1pl.witchcraft.core.builders.BaseRawMessageBuilder;
import com.ivan1pl.witchcraft.core.builders.ComplexRawMessageBuilder;
import com.ivan1pl.witchcraft.core.builders.MessageBuilder;
import com.ivan1pl.witchcraft.commands.annotations.*;
import com.ivan1pl.witchcraft.core.builders.RawMessageBuilder;
import com.ivan1pl.witchcraft.core.messages.ChatColor;
import com.ivan1pl.witchcraft.core.messages.actions.ClickEvent;
import com.ivan1pl.witchcraft.core.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Default help provider.
 */
class Help {
    /**
     * Display default help message.
     * @param commandSender command sender
     * @param commandName command name
     * @param commandDescription command description
     * @param firstArg first argument (possibly a subcommand or page number)
     * @param secondArg second argument (possibly a page number)
     * @param subcommands subcommands
     */
    static void help(CommandSender commandSender, String commandName, String commandDescription, String firstArg,
                     String secondArg, Map<String, MethodHolder> subcommands) {
        int pageNumber = getPageNumber(firstArg, secondArg);
        String subcommand = getSubCommand(firstArg);
        Map<String, Method> availableSubcommands = new HashMap<>();
        for (Map.Entry<String, MethodHolder> methodEntry : subcommands.entrySet()) {
            Method m = methodEntry.getValue().getOriginalMethod();
            if (m == null) {
                availableSubcommands.put(methodEntry.getKey(), null);
            } else {
                SubCommand subCommand = m.getAnnotation(SubCommand.class);
                if (subCommand == null || subCommand.permission().isEmpty() ||
                        commandSender.hasPermission(subCommand.permission())) {
                    availableSubcommands.put(methodEntry.getKey(), m);
                }
            }
        }
        boolean pagingEnabled = availableSubcommands.size() > 8;
        List<MessageBuilder> pages = new LinkedList<>();
        List<RawMessageBuilder> pagesRaw = new LinkedList<>();
        if (subcommand == null) {
            generalHelp(commandName, commandDescription, availableSubcommands, pages, pagesRaw);
        } else {
            Method subcommandMethod = availableSubcommands.get(subcommand);
            if (subcommandMethod == null) {
                MessageBuilder messageBuilder = new MessageBuilder();
                RawMessageBuilder rawMessageBuilder = new RawMessageBuilder();
                pages.add(messageBuilder);
                pagesRaw.add(rawMessageBuilder);
                messageBuilder
                        .color(ChatColor.RED)
                        .append("No help available for '").append(subcommand).append("'")
                        .resetColor();
                rawMessageBuilder
                        .color(ChatColor.RED)
                        .append("No help available for '").append(subcommand).append("'")
                        .resetColor();
            } else {
                detailedHelp(commandName, subcommand, subcommandMethod, pages, pagesRaw, true);
            }
        }
        String message;
        String messageRaw;
        if (pagingEnabled && pages.size() > 1) {
            int page = restrict(pageNumber, 1, pages.size());
            MessageBuilder pageBuilder = pages.get(page-1);
            RawMessageBuilder pageBuilderRaw = pagesRaw.get(page-1);
            message = new MessageBuilder()
                    .color(ChatColor.AQUA).append("Displaying Help [")
                    .color(ChatColor.GREEN).append(Integer.toString(page))
                    .color(ChatColor.AQUA).append("/").append(Integer.toString(pages.size())).append("]").newLine()
                    .append("To view other pages, type: /").append(commandName).append(" help")
                    .append(subcommand == null ? " " : " " + subcommand + " ")
                    .color(ChatColor.GREEN).append("<page number>").resetColor().newLine()
                    .append(pageBuilder.build()).build();
            pageBuilderRaw.reset().newLine().color(page == 1 ? ChatColor.DARK_GRAY : ChatColor.GOLD);
            RawMessageBuilder.HoverMessageBuilder<RawMessageBuilder> hoverBuilderPrev =
                    pageBuilderRaw.textHover().append("Click to change to previous page").text();
            if (page > 1) {
                hoverBuilderPrev
                        .action(ClickEvent.Action.RUN_COMMAND,
                                "/" + commandName + " help " +
                                        (subcommand == null ? "" : subcommand + " ") + (page - 1))
                        .append("<< Previous page")
                        .end().end();
            } else {
                hoverBuilderPrev.append("<< Previous page").end();
            }
            pageBuilderRaw.resetColor().append(" | ")
                    .color(page == pages.size() ? ChatColor.DARK_GRAY : ChatColor.GOLD);
            RawMessageBuilder.HoverMessageBuilder<RawMessageBuilder> hoverBuilderNext =
                    pageBuilderRaw.textHover().append("Click to change to next page").text();
            if (page < pages.size()) {
                hoverBuilderNext
                        .action(ClickEvent.Action.RUN_COMMAND,
                                "/" + commandName + " help " +
                                        (subcommand == null ? "" : subcommand + " ") + (page + 1))
                        .append("Next page >>")
                        .end().end();
            } else {
                hoverBuilderNext.append("Next page >>").end();
            }
            messageRaw = pageBuilderRaw.toString();
        } else {
            message = pages.stream().map(MessageBuilder::build).collect(Collectors.joining("\n"));
            messageRaw = new Gson().toJson(
                    pagesRaw.stream()
                            .map(BaseRawMessageBuilder::build)
                            .filter(Objects::nonNull)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList()));
        }
        if (commandSender instanceof Player) {
            MessageUtils.sendJsonMessage((Player) commandSender, messageRaw);
        } else {
            commandSender.sendMessage(message);
        }
    }

    /**
     * Display general command description and list of subcommands.
     * @param commandName command name
     * @param commandDescription command description
     * @param subcommands available subcommands
     * @param messageBuilders message builders
     * @param rawMessageBuilders raw message builders
     */
    private static void generalHelp(String commandName, String commandDescription, Map<String, Method> subcommands,
                                    List<MessageBuilder> messageBuilders, List<RawMessageBuilder> rawMessageBuilders) {
        List<Map.Entry<String, Method>> subcommandsSorted = new ArrayList<>(subcommands.entrySet());
        subcommandsSorted.sort(Map.Entry.comparingByKey());
        MessageBuilder messageBuilder = new MessageBuilder();
        RawMessageBuilder rawMessageBuilder = new RawMessageBuilder();
        messageBuilders.add(messageBuilder);
        rawMessageBuilders.add(rawMessageBuilder);

        if (subcommandsSorted.isEmpty() && (commandDescription == null || commandDescription.isEmpty())) {
            messageBuilder.color(ChatColor.RED).append("No help is available").resetColor();
            rawMessageBuilder.color(ChatColor.RED).append("No help is available").resetColor();
        } else {
            if (commandDescription != null && !commandDescription.isEmpty()) {
                messageBuilder
                        .color(ChatColor.AQUA).append("[")
                        .color(ChatColor.GREEN).append(commandName)
                        .color(ChatColor.AQUA).append("] ").append(commandDescription)
                        .resetColor().newLine();
                rawMessageBuilder
                        .color(ChatColor.AQUA).append("[")
                        .color(ChatColor.GREEN).append(commandName)
                        .color(ChatColor.AQUA).append("] ").append(commandDescription)
                        .resetColor().newLine();
            }
            int i = 0;
            if (subcommandsSorted.get(0).getKey().isEmpty()) {
                detailedHelp(commandName, null, subcommandsSorted.get(0).getValue(), messageBuilders,
                        rawMessageBuilders, false);
                messageBuilder = new MessageBuilder();
                rawMessageBuilder = new RawMessageBuilder();
                messageBuilders.add(messageBuilder);
                rawMessageBuilders.add(rawMessageBuilder);
                i++;
            }
            if (subcommandsSorted.size() > i) {
                int onPage = 0;
                messageBuilder
                        .color(ChatColor.AQUA).append("Use ")
                        .color(ChatColor.DARK_RED).append(commandName).append(" help <")
                        .color(ChatColor.RED).append("subcommand name").color(ChatColor.DARK_RED).append(">")
                        .color(ChatColor.AQUA).append(" to display detailed help for specific subcommand.")
                        .resetColor().newLine().color(ChatColor.AQUA).append("Available subcommands:")
                        .resetColor().newLine();
                rawMessageBuilder.color(ChatColor.AQUA).append("Available subcommands:").resetColor().newLine();
                for (; i < subcommandsSorted.size(); ++i) {
                    if (onPage >= 5) {
                        messageBuilder = new MessageBuilder();
                        rawMessageBuilder = new RawMessageBuilder();
                        messageBuilders.add(messageBuilder);
                        rawMessageBuilders.add(rawMessageBuilder);
                        onPage = 0;
                    }
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
                    rawMessageBuilder
                            .color(ChatColor.AQUA).append("> ").color(ChatColor.GREEN)
                            .textHover("Click to display detailed help about this subcommand")
                                .action(ClickEvent.Action.RUN_COMMAND, "/" + commandName + " help " + subcommandName)
                                    .append(subcommandName)
                                .end()
                            .end();
                    if (!description.isEmpty()) {
                        rawMessageBuilder.color(ChatColor.AQUA).append(" - ").append(description);
                    }
                    rawMessageBuilder.resetColor().newLine();
                    onPage++;
                }
            }
        }
    }

    /**
     * Display detailed description of a specific subcommand.
     * @param commandName command name
     * @param subcommandName subcommand name
     * @param commandMethod subcommand method
     * @param messageBuilders message builders
     * @param rawMessageBuilders raw message builders
     * @param startNewPage whether to start new page or continue the previous one
     */
    private static void detailedHelp(String commandName, String subcommandName, Method commandMethod,
                                     List<MessageBuilder> messageBuilders, List<RawMessageBuilder> rawMessageBuilders,
                                     boolean startNewPage) {
        MessageBuilder messageBuilder;
        RawMessageBuilder rawMessageBuilder;
        if (startNewPage) {
            messageBuilder = new MessageBuilder();
            messageBuilders.add(messageBuilder);
            rawMessageBuilder = new RawMessageBuilder();
            rawMessageBuilders.add(rawMessageBuilder);
        } else {
            messageBuilder = messageBuilders.get(0);
            rawMessageBuilder = rawMessageBuilders.get(0);
        }
        messageBuilder.color(ChatColor.AQUA).append("> ").color(ChatColor.GREEN).append(commandName);
        ComplexRawMessageBuilder.HoverMessageBuilder<ComplexRawMessageBuilder.ActionMessageBuilder<RawMessageBuilder>>
                commandNameBuilder =
                rawMessageBuilder.color(ChatColor.AQUA).append("> ").color(ChatColor.GREEN)
                        .action(ClickEvent.Action.SUGGEST_COMMAND,
                                "/" + commandName + (subcommandName == null ? " " : " " + subcommandName + " "))
                        .textHover("Click to enter command in chat").append(commandName);
        if (subcommandName != null && !subcommandName.isEmpty()) {
            messageBuilder.append(" ").append(subcommandName);
            commandNameBuilder.append(" ").append(subcommandName);
        }
        List<Parameter> options = new LinkedList<>();
        if (Arrays.stream(commandMethod.getParameters()).anyMatch(p -> p.isAnnotationPresent(Option.class))) {
            messageBuilder.append(" [options]");
            commandNameBuilder.append(" [options]");
        }
        int i = 0;
        for (Parameter parameter : commandMethod.getParameters()) {
            if (parameter.getAnnotation(Sender.class) == null &&
                    parameter.getAnnotation(ConfigurationValue.class) == null &&
                    parameter.getAnnotation(Option.class) == null) {
                String name = parameter.getName();
                Optional optional = parameter.getAnnotation(Optional.class);
                boolean vararg = i == commandMethod.getParameterCount() - 1 && parameter.getType().isArray();
                if (optional == null) {
                    messageBuilder.append(" <").append(name).append(vararg ? "..." : "").append(">");
                    commandNameBuilder.append(" <").append(name).append(vararg ? "..." : "").append(">");
                } else {
                    messageBuilder.append(" [").append(name).append("=").append(optional.value()).append("]");
                    commandNameBuilder.append(" [").append(name).append("=").append(optional.value()).append("]");
                }
            } else if (parameter.getAnnotation(Option.class) != null) {
                options.add(parameter);
            }
            i++;
        }
        commandNameBuilder.end().end();

        Description subcommandDescription = commandMethod.getAnnotation(Description.class);
        String shortDescription = subcommandDescription == null ? "" : subcommandDescription.shortDescription();
        String longDescription = subcommandDescription == null ? "" : subcommandDescription.detailedDescription();
        if (!shortDescription.isEmpty()) {
            messageBuilder.color(ChatColor.AQUA).append(" - ").append(shortDescription);
            rawMessageBuilder.color(ChatColor.AQUA).append(" - ").append(shortDescription);
        }
        messageBuilder.resetColor().newLine();
        rawMessageBuilder.resetColor().newLine();
        if (!longDescription.isEmpty()) {
            messageBuilder.color(ChatColor.AQUA).append(longDescription).resetColor().newLine();
            rawMessageBuilder.color(ChatColor.AQUA).append(longDescription).resetColor().newLine();
        }
        optionsHelp(options, messageBuilders, rawMessageBuilders);
    }

    /**
     * Generate options description.
     * @param options available options
     * @param messageBuilders message builders
     * @param rawMessageBuilders raw message builders
     */
    private static void optionsHelp(List<Parameter> options, List<MessageBuilder> messageBuilders,
                                    List<RawMessageBuilder> rawMessageBuilders) {
        int i = 0;
        int shortOptMaxLength = options.stream()
                .filter(o -> Character.isLetterOrDigit(o.getAnnotation(Option.class).shortName()))
                .mapToInt(o -> o.getType() == boolean.class || o.getType() == Boolean.class ?
                        2 : 5 + o.getName().length())
                .max().orElse(0);
        int longOptMaxLength = options.stream()
                .filter(o -> !o.getAnnotation(Option.class).longName().isEmpty())
                .mapToInt(o -> 2 + o.getAnnotation(Option.class).longName().length() +
                        (o.getType() == boolean.class || o.getType() == Boolean.class ? 0 : 3 + o.getName().length()))
                .max().orElse(0);
        MessageBuilder messageBuilder = null;
        RawMessageBuilder rawMessageBuilder = null;
        for (Parameter parameter : options) {
            Option option = parameter.getAnnotation(Option.class);
            if (i++ % 5 == 0) {
                messageBuilder = new MessageBuilder();
                rawMessageBuilder = new RawMessageBuilder();
                messageBuilders.add(messageBuilder);
                rawMessageBuilders.add(rawMessageBuilder);
                messageBuilder.color(ChatColor.GREEN).append("Available options:").resetColor().newLine();
                rawMessageBuilder.color(ChatColor.GREEN).append("Available options:").resetColor().newLine();
            }
            messageBuilder.append("  ");
            rawMessageBuilder.append("  ");
            if (shortOptMaxLength > 0) {
                if (Character.isLetterOrDigit(option.shortName())) {
                    messageBuilder
                            .color(ChatColor.RED)
                            .append("-" + option.shortName())
                            .append(parameter.getType() == boolean.class || parameter.getType() == Boolean.class ?
                                    Strings.repeat(" ", shortOptMaxLength - 2) :
                                    Strings.padEnd(" <" + parameter.getName() + ">", shortOptMaxLength - 2, ' '))
                            .resetColor()
                            .append("  ");
                    rawMessageBuilder
                            .color(ChatColor.RED)
                            .append("-" + option.shortName())
                            .append(parameter.getType() == boolean.class || parameter.getType() == Boolean.class ?
                                    Strings.repeat(" ", shortOptMaxLength - 2) :
                                    Strings.padEnd(" <" + parameter.getName() + ">", shortOptMaxLength - 2, ' '))
                            .resetColor()
                            .append("  ");
                } else {
                    messageBuilder.append(Strings.repeat(" ", shortOptMaxLength + 2));
                    rawMessageBuilder.append(Strings.repeat(" ", shortOptMaxLength + 2));
                }
            }
            if (longOptMaxLength > 0) {
                if (!option.longName().isEmpty()) {
                    messageBuilder
                            .color(ChatColor.RED)
                            .append("--" + option.longName())
                            .append(parameter.getType() == boolean.class || parameter.getType() == Boolean.class ?
                                    Strings.repeat(" ", longOptMaxLength - 2 - option.longName().length()) :
                                    Strings.padEnd(" <" + parameter.getName() + ">",
                                            longOptMaxLength - 2 - option.longName().length(), ' '))
                            .resetColor()
                            .append("  ");
                    rawMessageBuilder
                            .color(ChatColor.RED)
                            .append("--" + option.longName())
                            .append(parameter.getType() == boolean.class || parameter.getType() == Boolean.class ?
                                    Strings.repeat(" ", longOptMaxLength - 2 - option.longName().length()) :
                                    Strings.padEnd(" <" + parameter.getName() + ">",
                                            longOptMaxLength - 2 - option.longName().length(), ' '))
                            .resetColor()
                            .append("  ");
                } else {
                    messageBuilder.append(Strings.repeat(" ", longOptMaxLength + 2));
                    rawMessageBuilder.append(Strings.repeat(" ", longOptMaxLength + 2));
                }
            }
            if (!option.description().isEmpty()) {
                messageBuilder.color(ChatColor.AQUA).append(option.description()).resetColor().newLine();
                rawMessageBuilder.color(ChatColor.AQUA).append(option.description()).resetColor().newLine();
            }
        }
    }

    /**
     * Get page number from command parameters.
     * @param firstArg first parameter
     * @param secondArg second parameter
     * @return help page number
     */
    private static int getPageNumber(String firstArg, String secondArg) {
        int page;
        try {
            page = Integer.parseInt(secondArg);
        } catch (NumberFormatException e) {
            try {
                page = Integer.parseInt(firstArg);
            } catch (NumberFormatException e1) {
                page = 1;
            }
        }
        return page;
    }

    /**
     * Get subcommand name from command parameters.
     * @param firstArg first parameter
     * @return subcommand name
     */
    private static String getSubCommand(String firstArg) {
        try {
            Integer.parseInt(firstArg);
            return null;
        } catch (NumberFormatException e) {
            return firstArg;
        }
    }

    /**
     * Restrict int value to given range.
     * @param value value
     * @param min range start
     * @param max range end
     * @return value restricted to given range
     */
    private static int restrict(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }
}
