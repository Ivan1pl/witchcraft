package com.ivan1pl.witchcraft.commands.base;

import com.ivan1pl.witchcraft.commands.annotations.Plugin;
import com.ivan1pl.witchcraft.commands.exceptions.CommandAlreadyExistsException;
import com.ivan1pl.witchcraft.commands.exceptions.CommandDefinitionNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Command executor for all commands generated with {@link com.ivan1pl.witchcraft.commands.annotations.Command} annotation.
 */
public class AnnotationBasedCommandExecutor implements TabExecutor {
    private final Map<String, CommandHolder> commands = new HashMap<>();

    /**
     * Default constructor.
     * @param javaPlugin plugin instance
     */
    public AnnotationBasedCommandExecutor(JavaPlugin javaPlugin) throws CommandAlreadyExistsException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            CommandDefinitionNotFoundException {
        String basePackage = javaPlugin.getClass().getPackage() == null ?
                null : javaPlugin.getClass().getPackage().getName();
        Plugin plugin = javaPlugin.getClass().getAnnotation(Plugin.class);
        if (plugin != null && !plugin.basePackage().isEmpty()) {
            basePackage = plugin.basePackage();
        }
        javaPlugin.getLogger().info(String.format("Starting package scan for package: %s", basePackage));
        Set<Class<?>> commands = new Reflections(basePackage)
                .getTypesAnnotatedWith(com.ivan1pl.witchcraft.commands.annotations.Command.class);
        for (Class<?> commandClass : commands) {
            com.ivan1pl.witchcraft.commands.annotations.Command command =
                    commandClass.getAnnotation(com.ivan1pl.witchcraft.commands.annotations.Command.class);
            if (command != null && !command.name().isEmpty()) {
                javaPlugin.getLogger().info(String.format("Processing command: %s", command.name().toLowerCase()));
                CommandHolder holder = this.commands.get(command.name().toLowerCase());
                if (holder != null) {
                    throw new CommandAlreadyExistsException(
                            String.format("Command %s already exists", command.name().toLowerCase()));
                }
                holder = new CommandHolder(javaPlugin, command.name().toLowerCase(), command.description(),
                        commandClass);
                this.commands.put(command.name().toLowerCase(), holder);
                javaPlugin.getLogger().info(String.format("Registered command: %s", command.name().toLowerCase()));
                PluginCommand pluginCommand = javaPlugin.getCommand(command.name().toLowerCase());
                if (pluginCommand != null) {
                    pluginCommand.setExecutor(this);
                    pluginCommand.setTabCompleter(this);
                } else {
                    throw new CommandDefinitionNotFoundException("Command definition is missing from plugin.yml");
                }
                javaPlugin.getLogger().info(String.format(
                        "Registered %s as command executor and tab completer for command %s",
                        AnnotationBasedCommandExecutor.class.getCanonicalName(), command.name().toLowerCase()));
            }
        }
    }

    /**
     * Deduce which command to execute and run it.
     * @param sender command sender
     * @param command command
     * @param label command label
     * @param args command parameters
     * @return {@code true} if execution succeeded, {@code false} otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();
        CommandHolder holder = commands.get(commandName);
        if (holder != null) {
            return holder.match(sender, args);
        }
        return false;
    }

    /**
     * Suggest completions for last partial value.
     * @param sender command sender
     * @param command command
     * @param alias command alias
     * @param args command parameters
     * @return list of suggested values
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String commandName = command.getName();
        CommandHolder holder = commands.get(commandName);
        if (holder != null) {
            return toList(holder.getTabCompletions(sender, args));
        }
        return new ArrayList<>();
    }

    /**
     * Sort a set into a list.
     * @param set set
     * @return sorted list
     */
    private static List<String> toList(Set<String> set) {
        List<String> list = new ArrayList<>(set);
        Collections.sort(list);
        return list;
    }

    /**
     * Disable command executor.
     */
    public void disable() {
        commands.clear();
    }
}
