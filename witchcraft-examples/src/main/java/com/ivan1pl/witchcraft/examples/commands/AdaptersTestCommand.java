package com.ivan1pl.witchcraft.examples.commands;

import com.ivan1pl.witchcraft.commands.annotations.Command;
import com.ivan1pl.witchcraft.commands.annotations.Description;
import com.ivan1pl.witchcraft.commands.annotations.Sender;
import com.ivan1pl.witchcraft.commands.annotations.SubCommand;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionType;

@Command(name = "wcadapter", description = "Test command for new types of adapters and type completers")
public class AdaptersTestCommand {
    @SubCommand("biome")
    @Description(shortDescription = "Test biome adapter", detailedDescription = "Test biome adapter")
    public void biome(@Sender CommandSender commandSender, Biome biome) {
        commandSender.sendMessage("Biome: " + biome.name());
    }

    @SubCommand("block-data")
    @Description(shortDescription = "Test block data adapter", detailedDescription = "Test block data adapter")
    public void blockData(@Sender CommandSender commandSender, BlockData blockData) {
        commandSender.sendMessage("Block data: " + blockData.getAsString(true));
    }

    @SubCommand("entity-effect")
    @Description(shortDescription = "Test entity effect adapter", detailedDescription = "Test entity effect adapter")
    public void entityEffect(@Sender CommandSender commandSender, EntityEffect entityEffect) {
        commandSender.sendMessage("Entity effectt: " + entityEffect.name());
    }

    @SubCommand("entity-type")
    @Description(shortDescription = "Test entity type adapter", detailedDescription = "Test entity type adapter")
    public void entityType(@Sender CommandSender commandSender, EntityType entityType) {
        commandSender.sendMessage("Entity type: " + entityType.name());
    }

    @SubCommand("equipment-slot")
    @Description(shortDescription = "Test equipment slot type adapter",
            detailedDescription = "Test equipment slot type adapter")
    public void equipmentSlot(@Sender CommandSender commandSender, EquipmentSlot equipmentSlot) {
        commandSender.sendMessage("Equipment slot: " + equipmentSlot.name());
    }

    @SubCommand("game-mode")
    @Description(shortDescription = "Test game mode adapter", detailedDescription = "Test game mode adapter")
    public void gameMode(@Sender CommandSender commandSender, GameMode gameMode) {
        commandSender.sendMessage("Game mode: " + gameMode.name());
    }

    @SubCommand("instrument")
    @Description(shortDescription = "Test instrument adapter", detailedDescription = "Test instrument adapter")
    public void instrument(@Sender CommandSender commandSender, Instrument instrument) {
        commandSender.sendMessage("Instrument: " + instrument.name());
    }

    @SubCommand("material")
    @Description(shortDescription = "Test material adapter", detailedDescription = "Test material adapter")
    public void material(@Sender CommandSender commandSender, Material material) {
        commandSender.sendMessage("Material: " + material.name());
    }

    @SubCommand("potion-type")
    @Description(shortDescription = "Test potion type adapter", detailedDescription = "Test potion type adapter")
    public void potionType(@Sender CommandSender commandSender, PotionType potionType) {
        commandSender.sendMessage("Potion type: " + potionType.name());
    }

    @SubCommand("weather-type")
    @Description(shortDescription = "Test weather type adapter", detailedDescription = "Test weather type adapter")
    public void weatherType(@Sender CommandSender commandSender, WeatherType weatherType) {
        commandSender.sendMessage("Weather type: " + weatherType.name());
    }

    @SubCommand("world")
    @Description(shortDescription = "Test world adapter", detailedDescription = "Test world adapter")
    public void world(@Sender CommandSender commandSender, World world) {
        commandSender.sendMessage("World: " + world.getName());
    }

    @SubCommand("world-type")
    @Description(shortDescription = "Test world type adapter", detailedDescription = "Test world type adapter")
    public void worldType(@Sender CommandSender commandSender, WorldType worldType) {
        commandSender.sendMessage("World type: " + worldType.name());
    }
}
