package com.ivan1pl.witchcraft.examples.commands;

import com.ivan1pl.witchcraft.commands.annotations.Command;
import com.ivan1pl.witchcraft.commands.annotations.Description;
import com.ivan1pl.witchcraft.commands.annotations.Sender;
import com.ivan1pl.witchcraft.commands.annotations.SubCommand;
import com.ivan1pl.witchcraft.examples.data.Counter;
import org.bukkit.command.CommandSender;

@Command(name = "csubtract", description = "Decrease counter")
public class WcSubtract {
    private final Counter counter;

    public WcSubtract(Counter counter) {
        this.counter = counter;
    }

    @SubCommand
    @Description(shortDescription = "Decrease counter",
            detailedDescription = "Subtract 1 from current value of the global counter")
    public void decreaseCounter(@Sender CommandSender commandSender) {
        counter.decrease();
        commandSender.sendMessage("Decreased counter, current value: " + counter.getValue());
    }
}
