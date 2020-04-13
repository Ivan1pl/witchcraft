package com.ivan1pl.netherite.examples.commands;

import com.ivan1pl.spigot.utils.commands.annotations.Command;
import com.ivan1pl.spigot.utils.commands.annotations.Optional;
import com.ivan1pl.spigot.utils.commands.annotations.Sender;
import com.ivan1pl.spigot.utils.commands.annotations.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@Command(name = "spigot-utils-test", aliases = {"sutest", "sptest"},
        permission = "spigot-utils-example-plugin.spigot-utils-test",
        description = "Test command for spigot-utils-examples")
public class TestCommand {

    @SubCommand(value = "any-sender", permission = "spigot-utils-example-plugin.spigot-utils-test.any")
    public void anySenderCommand(@Sender CommandSender commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "any-sender", player.getName(), number, otherNumber));
    }

    @SubCommand(value = "player-sender", permission = "spigot-utils-example-plugin.spigot-utils-test.player")
    public void playerSenderCommand(@Sender Player commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "player-sender", player.getName(), number, otherNumber));
    }

    @SubCommand(value = "console-sender", permission = "spigot-utils-example-plugin.spigot-utils-test.console")
    public void consoleSenderCommand(@Sender ConsoleCommandSender commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "console-sender", player.getName(), number, otherNumber));
    }
}
