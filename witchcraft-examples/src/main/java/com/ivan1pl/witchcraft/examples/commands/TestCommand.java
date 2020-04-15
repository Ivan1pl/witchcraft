package com.ivan1pl.witchcraft.examples.commands;

import com.ivan1pl.witchcraft.commands.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@Command(name = "witchcraft-test", aliases = {"wtest", "wctest"},
        permission = "witchcraft-example-plugin.witchcraft-test",
        description = "Test command for witchcraft-examples")
public class TestCommand {

    @SubCommand("")
    @Description(shortDescription = "Short description for witchcraft-test", detailedDescription = "Detailed description for witchcraft-test")
    public void witchcraftTest(@Sender CommandSender commandSender, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing default command; params: number=%d, otherNumber=%d", number, otherNumber));
    }

    @SubCommand(value = "any-sender", permission = "witchcraft-example-plugin.witchcraft-test.any")
    @Description(shortDescription = "Short description for any-sender", detailedDescription = "Detailed description for any-sender")
    public void anySenderCommand(@Sender CommandSender commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "any-sender", player.getName(), number, otherNumber));
    }

    @SubCommand(value = "player-sender", permission = "witchcraft-example-plugin.witchcraft-test.player")
    @Description(shortDescription = "Short description for player-sender", detailedDescription = "Detailed description for player-sender")
    public void playerSenderCommand(@Sender Player commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "player-sender", player.getName(), number, otherNumber));
    }

    @SubCommand(value = "console-sender", permission = "witchcraft-example-plugin.witchcraft-test.console")
    @Description(shortDescription = "Short description for console-sender", detailedDescription = "Detailed description for console-sender")
    public void consoleSenderCommand(@Sender ConsoleCommandSender commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "console-sender", player.getName(), number, otherNumber));
    }
}
