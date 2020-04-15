package com.ivan1pl.witchcraft.commands.exceptions;

import com.ivan1pl.witchcraft.commands.annotations.Command;

/**
 * Exception thrown when two classes are annotated with {@link Command} annotation
 * with the same name.
 */
public class CommandAlreadyExistsException extends Exception {
    /**
     * Create exception instance.
     * @param message exception message
     */
    public CommandAlreadyExistsException(String message) {
        super(message);
    }
}
