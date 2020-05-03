package com.ivan1pl.witchcraft.commands.base;

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
                     String secondArg, Map<String, Method> subcommands) {
        int pageNumber = getPageNumber(firstArg, secondArg);
        String subcommand = getSubCommand(firstArg);
        Map<String, Method> availableSubcommands = new HashMap<>();
        for (Map.Entry<String, Method> methodEntry : subcommands.entrySet()) {
            Method m = methodEntry.getValue();
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
            MessageBuilder messageBuilder = new MessageBuilder();
            RawMessageBuilder rawMessageBuilder = new RawMessageBuilder();
            pages.add(messageBuilder);
            pagesRaw.add(rawMessageBuilder);
            Method subcommandMethod = availableSubcommands.get(subcommand);
            if (subcommandMethod == null) {
                messageBuilder
                        .color(ChatColor.RED)
                        .append("No help available for '").append(subcommand).append("'")
                        .resetColor();
                rawMessageBuilder
                        .color(ChatColor.RED)
                        .append("No help available for '").append(subcommand).append("'")
                        .resetColor();
            } else {
                detailedHelp(commandName, subcommand, subcommandMethod, messageBuilder, rawMessageBuilder);
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
                detailedHelp(commandName, null, subcommandsSorted.get(0).getValue(), messageBuilder,
                        rawMessageBuilder);
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
     * @param messageBuilder message builder
     * @param rawMessageBuilder raw message builder
     */
    private static void detailedHelp(String commandName, String subcommandName, Method commandMethod,
                                     MessageBuilder messageBuilder, RawMessageBuilder rawMessageBuilder) {
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
        for (Parameter parameter : commandMethod.getParameters()) {
            if (parameter.getAnnotation(Sender.class) == null &&
                    parameter.getAnnotation(ConfigurationValue.class) == null) {
                String name = parameter.getName();
                Optional optional = parameter.getAnnotation(Optional.class);
                if (optional == null) {
                    messageBuilder.append(" <").append(name).append(">");
                    commandNameBuilder.append(" <").append(name).append(">");
                } else {
                    messageBuilder.append(" [").append(name).append("=").append(optional.value()).append("]");
                    commandNameBuilder.append(" [").append(name).append("=").append(optional.value()).append("]");
                }
            }
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
