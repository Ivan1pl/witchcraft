package com.ivan1pl.witchcraft.commands.exceptions;

/**
 * Exception thrown when command definition is missing from {@code plugin.yml} file.
 */
public class CommandDefinitionNotFoundException extends Exception {
    /**
     * Create exception instance.
     * @param message exception message
     */
    public CommandDefinitionNotFoundException(String message) {
        super(message);
    }
}
