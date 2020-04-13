package com.ivan1pl.netherite.commands.base;

/**
 * Adapter interface used to create adapters for command parameters.
 */
public interface TypeAdapter {
    /**
     * Convert a {@code String} to a desired type.
     * @param arg value to convert
     * @return conversion result
     */
    Object convert(String arg);
}
