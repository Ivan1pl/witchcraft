package com.ivan1pl.witchcraft.examples.commands;

import com.ivan1pl.witchcraft.commands.annotations.*;
import com.ivan1pl.witchcraft.examples.WitchCraftExamplePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Command(name = "witchcraft-test", aliases = {"wtest", "wctest"},
        permission = "witchcraft-example-plugin.witchcraft-test",
        description = "Test command for witchcraft-examples")
public class TestCommand {
    private final WitchCraftExamplePlugin witchCraftExamplePlugin;

    public TestCommand(WitchCraftExamplePlugin witchCraftExamplePlugin) {
        this.witchCraftExamplePlugin = witchCraftExamplePlugin;
    }

    @SubCommand("")
    @Description(shortDescription = "Short description for witchcraft-test", detailedDescription = "Detailed description for witchcraft-test")
    public void witchcraftTest(@Sender CommandSender commandSender, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing default command; params: number=%d, otherNumber=%d", number, otherNumber));
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand(value = "any-sender", permission = "witchcraft-example-plugin.witchcraft-test.any")
    @Description(shortDescription = "Short description for any-sender", detailedDescription = "Detailed description for any-sender")
    public void anySenderCommand(@Sender CommandSender commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "any-sender", player.getName(), number, otherNumber));
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand(value = "player-sender", permission = "witchcraft-example-plugin.witchcraft-test.player")
    @Description(shortDescription = "Short description for player-sender", detailedDescription = "Detailed description for player-sender")
    public void playerSenderCommand(@Sender Player commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "player-sender", player.getName(), number, otherNumber));
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand(value = "console-sender", permission = "witchcraft-example-plugin.witchcraft-test.console")
    @Description(shortDescription = "Short description for console-sender", detailedDescription = "Detailed description for console-sender")
    public void consoleSenderCommand(@Sender ConsoleCommandSender commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "console-sender", player.getName(), number, otherNumber));
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub1")
    @Description(shortDescription = "sub1 short", detailedDescription = "sub1 detailed")
    public void sub1() {
        Bukkit.broadcastMessage("[sub1] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub2")
    @Description(shortDescription = "sub2 short", detailedDescription = "sub2 detailed")
    public void sub2() {
        Bukkit.broadcastMessage("[sub2] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub3")
    @Description(shortDescription = "sub3 short", detailedDescription = "sub3 detailed")
    public void sub3() {
        Bukkit.broadcastMessage("[sub3] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub4")
    @Description(shortDescription = "sub4 short", detailedDescription = "sub4 detailed")
    public void sub4() {
        Bukkit.broadcastMessage("[sub4] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub5")
    @Description(shortDescription = "sub5 short", detailedDescription = "sub5 detailed")
    public void sub5() {
        Bukkit.broadcastMessage("[sub5] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub6")
    @Description(shortDescription = "sub6 short", detailedDescription = "sub6 detailed")
    public void sub6() {
        Bukkit.broadcastMessage("[sub6] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub7")
    @Description(shortDescription = "sub7 short", detailedDescription = "sub7 detailed")
    public void sub7() {
        Bukkit.broadcastMessage("[sub7] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub8")
    @Description(shortDescription = "sub8 short", detailedDescription = "sub8 detailed")
    public void sub8() {
        Bukkit.broadcastMessage("[sub8] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub9")
    @Description(shortDescription = "sub9 short", detailedDescription = "sub9 detailed")
    public void sub9() {
        Bukkit.broadcastMessage("[sub9] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub10")
    @Description(shortDescription = "sub10 short", detailedDescription = "sub10 detailed")
    public void sub10() {
        Bukkit.broadcastMessage("[sub10] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub11")
    @Description(shortDescription = "sub11 short", detailedDescription = "sub11 detailed")
    public void sub11() {
        Bukkit.broadcastMessage("[sub11] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("sub12")
    @Description(shortDescription = "sub12 short", detailedDescription = "sub12 detailed")
    public void sub12() {
        Bukkit.broadcastMessage("[sub12] 42");
        witchCraftExamplePlugin.getLogger().info("Executed witchcraft-test");
    }

    @SubCommand("vararg1")
    @Description(shortDescription = "Varargs test", detailedDescription = "Varargs test")
    public void vararg1(@Sender CommandSender commandSender, int num, double[] doubles) {
        commandSender.sendMessage("num=" + num + ";doubles=" + Arrays.toString(doubles));
    }

    @SubCommand("vararg2")
    @Description(shortDescription = "Vararg test 2", detailedDescription = "Vararg test 2")
    public void vararg2(@Sender CommandSender commandSender, String someText, Material[] materials) {
        commandSender.sendMessage("someText=" + someText + ";materials=" + Arrays.toString(materials));
    }

    @SubCommand("opt")
    @Description(shortDescription = "Options test", detailedDescription = "Options test")
    public void opt(@Sender CommandSender commandSender, Material material1, Material material2,
                    @Option(shortName = 'a', longName = "all", description = "All option") boolean optA,
                    @Option(longName = "list", description = "List option") boolean optL,
                    @Option(shortName = 'x', longName = "extra", max = 3, description = "Additional materials") Material[] parX,
                    @Option(shortName = 'w', description = "World") World parW) {
        commandSender.sendMessage("material1=" + material1 + ";material2=" + material2 + ";all=" + optA + ";list=" +
                optL + ";extra=" + Arrays.toString(parX) + ";world=" + (parW == null ? "null" : parW.getName()));
    }

    @SubCommand("manyOpts")
    @Description(shortDescription = "Many options", detailedDescription = "Many options")
    public void manyOpts(@Option(shortName = 'a', longName = "a-option", description = "A option") String a,
                         @Option(shortName = 'b', longName = "b-option", description = "B option") boolean b,
                         @Option(shortName = 'c', description = "C option") String c,
                         @Option(longName = "d-option", description = "D option") String d,
                         @Option(shortName = 'e', description = "E option") boolean e,
                         @Option(longName = "f-option", description = "F option") boolean f,
                         @Option(shortName = 'g', description = "G option") String g,
                         @Option(shortName = 'h', longName = "h-option", description = "H option") String h) {}
}
