package com.ivan1pl.witchcraft.examples.commands;

import com.ivan1pl.witchcraft.commands.annotations.Command;
import com.ivan1pl.witchcraft.commands.annotations.Description;
import com.ivan1pl.witchcraft.commands.annotations.Sender;
import com.ivan1pl.witchcraft.commands.annotations.SubCommand;
import com.ivan1pl.witchcraft.examples.data.Counter;
import org.bukkit.command.CommandSender;

@Command(name = "cadd", description = "Increase counter")
public class WcAdd {
    private final Counter counter;

    public WcAdd(Counter counter) {
        this.counter = counter;
    }

    @SubCommand
    @Description(shortDescription = "Increase counter",
            detailedDescription = "Add 1 to current value of the global counter")
    public void increaseCounter(@Sender CommandSender commandSender) {
        counter.increase();
        commandSender.sendMessage("Increased counter, current value: " + counter.getValue());
    }
}
