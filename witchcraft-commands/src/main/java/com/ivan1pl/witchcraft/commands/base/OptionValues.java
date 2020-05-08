package com.ivan1pl.witchcraft.commands.base;

import com.ivan1pl.witchcraft.commands.annotations.Option;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents all option values that can be passed to a command.
 */
public class OptionValues {
    private final Map<Character, OptionValue> byShortName = new HashMap<>();
    private final Map<String, OptionValue> byLongName = new HashMap<>();

    /**
     * Add new option to command configuration.
     * @param option option to add
     * @param expectedType option's expected type
     */
    public void addOption(Option option, Class<?> expectedType) {
        OptionValue optionValue = new OptionValue(option.shortName(), option.longName(),
                expectedType != Boolean.class && expectedType != boolean.class, option.max());
        if (Character.isLetterOrDigit(option.shortName())) {
            addByShortName(option.shortName(), optionValue);
        }
        if (!option.longName().isEmpty()) {
            addByLongName(option.longName(), optionValue);
        }
    }

    /**
     * Add option by short name.
     * @param shortName short name
     * @param optionValue option to add
     */
    private void addByShortName(Character shortName, OptionValue optionValue) {
        if (byShortName.get(shortName) != null) {
            throw new IllegalArgumentException("Option: " + shortName + " was already defined for this command");
        }
        byShortName.put(shortName, optionValue);
    }

    /**
     * Add option by long name.
     * @param longName long name
     * @param optionValue option to add
     */
    private void addByLongName(String longName, OptionValue optionValue) {
        if (byLongName.get(longName) != null) {
            throw new IllegalArgumentException("Option: " + longName + " was already defined for this command");
        }
        byLongName.put(longName, optionValue);
    }

    /**
     * Get option by short name.
     * @param shortName short name
     * @return option with given short name
     */
    public OptionValue getOption(char shortName) {
        return byShortName.get(shortName);
    }

    /**
     * Get option by long name.
     * @param longName long name
     * @return option with given long name
     */
    public OptionValue getOption(String longName) {
        return byLongName.get(longName);
    }

    /**
     * Get option.
     * @param option option definition
     * @return option created from given definition
     */
    public OptionValue getOption(Option option) {
        if (Character.isLetterOrDigit(option.shortName())) {
            return getOption(option.shortName());
        }
        if (!option.longName().isEmpty()) {
            return getOption(option.longName());
        }
        return null;
    }

    /**
     * Get all possible option keys.
     * @return list of option keys
     */
    public List<String> getPossibleKeys() {
        return Stream.concat(
                byShortName.keySet().stream().map(c -> "-" + c),
                byLongName.keySet().stream().map(n -> "--" + n)).collect(Collectors.toList());
    }
}
