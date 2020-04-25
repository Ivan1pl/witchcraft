package com.ivan1pl.witchcraft.core.builders;

import com.google.gson.Gson;
import com.ivan1pl.witchcraft.core.messages.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Base builder class for raw JSON messages.
 */
public abstract class BaseRawMessageBuilder<T> {
    /**
     * All message components.
     */
    private final List<BaseRawMessage> components = new LinkedList<>();

    /**
     * Current message component.
     */
    private BaseRawMessage current;

    /**
     * Append text.
     * @param value text to append
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T append(String value) {
        if (value == null || value.isEmpty()) {
            return (T) this;
        }
        if (current == null) {
            current = new TextRawMessage();
            components.add(current);
        } else if (current.isEmpty()) {
            newCurrent(TextRawMessage.from(current));
        } else if (!(current instanceof TextRawMessage)) {
            newCurrent(new TextRawMessage());
        }
        TextRawMessage currentText = (TextRawMessage) current;
        currentText.setText(currentText.getText() + value);
        return (T) this;
    }

    /**
     * Append keybind.
     * @param value keybind to append
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T append(Keybind value) {
        if (value == null) {
            return (T) this;
        }
        if (current == null) {
            current = new KeybindRawMessage();
            components.add(current);
        } else if (current.isEmpty()) {
            newCurrent(KeybindRawMessage.from(current));
        } else {
            newCurrent(new KeybindRawMessage());
        }
        KeybindRawMessage currentKeybind = (KeybindRawMessage) current;
        currentKeybind.setKeybind(value);
        return (T) this;
    }

    /**
     * Append score.
     * @param value score to append
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T append(Score value) {
        if (value == null) {
            return (T) this;
        }
        if (current == null) {
            current = new ScoreRawMessage();
            components.add(current);
        } else if (current.isEmpty()) {
            newCurrent(ScoreRawMessage.from(current));
        } else {
            newCurrent(new ScoreRawMessage());
        }
        ScoreRawMessage currentScore = (ScoreRawMessage) current;
        currentScore.setScore(value);
        return (T) this;
    }

    /**
     * Append selector.
     * @param value selector to append
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T appendSelector(String value) {
        if (value == null) {
            return (T) this;
        }
        if (current == null) {
            current = new SelectorRawMessage();
            components.add(current);
        } else if (current.isEmpty()) {
            newCurrent(SelectorRawMessage.from(current));
        } else {
            newCurrent(new SelectorRawMessage());
        }
        SelectorRawMessage currentSelector = (SelectorRawMessage) current;
        currentSelector.setSelector(value);
        return (T) this;
    }

    /**
     * Append selector.
     * @param value nbt to append
     * @param interpret interpret modifier
     * @param block block entity coordinates
     * @param entity target selector for the entity
     * @param storage namespaced ID of the command storage
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T appendNbt(String value, boolean interpret, String block, String entity, String storage) {
        if (value == null) {
            return (T) this;
        }
        if (current == null) {
            current = new NbtRawMessage();
            components.add(current);
        } else if (current.isEmpty()) {
            newCurrent(NbtRawMessage.from(current));
        } else {
            newCurrent(new NbtRawMessage());
        }
        NbtRawMessage currentNbt = (NbtRawMessage) current;
        currentNbt.setNbt(value);
        currentNbt.setInterpret(interpret);
        currentNbt.setBlock(block);
        currentNbt.setEntity(entity);
        currentNbt.setStorage(storage);
        return (T) this;
    }

    /**
     * Append translation.
     * @param value translation to append
     * @param parameters translation parameters
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T appendTranslation(String value, BaseRawMessage... parameters) {
        if (value == null || value.isEmpty()) {
            return (T) this;
        }
        if (current == null) {
            current = new TranslationRawMessage();
            components.add(current);
        } else if (current.isEmpty()) {
            newCurrent(TranslationRawMessage.from(current));
        } else {
            newCurrent(new TranslationRawMessage());
        }
        TranslationRawMessage currentTranslation = (TranslationRawMessage) current;
        currentTranslation.setTranslate(value);
        if (parameters.length > 0) {
            if (currentTranslation.getWith() == null) {
                currentTranslation.setWith(new ArrayList<>(parameters.length));
            }
            Collections.addAll(currentTranslation.getWith(), parameters);
        }
        return (T) this;
    }

    /**
     * Set message color.
     * @param chatColor new color
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T color(ChatColor chatColor) {
        if (chatColor == null) {
            chatColor = ChatColor.RESET;
        }
        if (current == null) {
            current = new TextRawMessage();
            components.add(current);
        } else if (current.getColor() == chatColor) {
            return (T) this;
        } else if (!current.isEmpty()) {
            newCurrent(new TextRawMessage());
        }
        current.setColor(chatColor);
        return (T) this;
    }

    /**
     * Set message color.
     * @param chatColor new color
     * @return builder instance
     */
    public T color(org.bukkit.ChatColor chatColor) {
        return color(ChatColor.valueOf(chatColor));
    }

    /**
     * Reset message color to default.
     * @return builder instance
     */
    public T resetColor() {
        return color(ChatColor.RESET);
    }

    /**
     * Choose whether to render text in bold. Defaults to false.
     * @param bold bold modifier
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T bold(boolean bold) {
        if (current == null) {
            current = new TextRawMessage();
            components.add(current);
        } else if (current.getBold() != null && current.getBold() == bold) {
            return (T) this;
        } else if (!current.isEmpty()) {
            newCurrent(new TextRawMessage());
        }
        current.setBold(bold);
        return (T) this;
    }

    /**
     * Render text in bold.
     * @return builder instance
     */
    public T bold() {
        return bold(true);
    }

    /**
     * Choose whether to render text in italics. Defaults to false.
     * @param italic italic modifier
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T italic(boolean italic) {
        if (current == null) {
            current = new TextRawMessage();
            components.add(current);
        } else if (current.getItalic() != null && current.getItalic() == italic) {
            return (T) this;
        } else if (!current.isEmpty()) {
            newCurrent(new TextRawMessage());
        }
        current.setItalic(italic);
        return (T) this;
    }

    /**
     * Render text in italics.
     * @return builder instance
     */
    public T italic() {
        return italic(true);
    }

    /**
     * Choose whether to render text underlined. Defaults to false.
     * @param underlined underlined modifier
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T underlined(boolean underlined) {
        if (current == null) {
            current = new TextRawMessage();
            components.add(current);
        } else if (current.getUnderlined() != null && current.getUnderlined() == underlined) {
            return (T) this;
        } else if (!current.isEmpty()) {
            newCurrent(new TextRawMessage());
        }
        current.setUnderlined(underlined);
        return (T) this;
    }

    /**
     * Render text underlined.
     * @return builder instance
     */
    public T underlined() {
        return underlined(true);
    }

    /**
     * Choose whether to render text with a strikethrough. Defaults to false.
     * @param strikethrough strikethrough modifier
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T strikethrough(boolean strikethrough) {
        if (current == null) {
            current = new TextRawMessage();
            components.add(current);
        } else if (current.getStrikethrough() != null && current.getStrikethrough() == strikethrough) {
            return (T) this;
        } else if (!current.isEmpty()) {
            newCurrent(new TextRawMessage());
        }
        current.setStrikethrough(strikethrough);
        return (T) this;
    }

    /**
     * Render text with a strikethrough.
     * @return builder instance
     */
    public T strikethrough() {
        return strikethrough(true);
    }

    /**
     * Choose whether to render text obfuscated. Defaults to false.
     * @param obfuscated obfuscated modifier
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T obfuscated(boolean obfuscated) {
        if (current == null) {
            current = new TextRawMessage();
            components.add(current);
        } else if (current.getObfuscated() != null && current.getObfuscated() == obfuscated) {
            return (T) this;
        } else if (!current.isEmpty()) {
            newCurrent(new TextRawMessage());
        }
        current.setObfuscated(obfuscated);
        return (T) this;
    }

    /**
     * Render text obfuscated.
     * @return builder instance
     */
    public T obfuscated() {
        return obfuscated(true);
    }

    /**
     * When the text is shift-clicked by a player, this string is inserted in their chat input. It does not overwrite
     * any existing text the player was writing.
     * @param insertion insertion
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T insertion(String insertion) {
        if (current == null) {
            current = new TextRawMessage();
            components.add(current);
        } else if (!current.isEmpty()) {
            newCurrent(new TextRawMessage());
        }
        current.setInsertion(insertion);
        return (T) this;
    }

    /**
     * Break line.
     * @return builder instance
     */
    public T newLine() {
        return append("\n");
    }

    /**
     * Reset all formatting.
     * @return builder instance
     */
    @SuppressWarnings("unchecked")
    public T reset() {
        current = null;
        return (T) this;
    }

    /**
     * Build message.
     * @return message
     */
    public List<BaseRawMessage> build() {
        return clearEmpty(components);
    }

    /**
     * Build JSON message.
     * @return JSON message
     */
    public String toString() {
        List<BaseRawMessage> parts = clearEmpty(components);
        if (parts == null || parts.isEmpty()) {
            return "";
        } else if (parts.size() == 1) {
            return new Gson().toJson(parts.get(0));
        } else {
            return new Gson().toJson(parts);
        }
    }

    /**
     * Create new component, append it to current component's extra and set it as current component.
     */
    protected final void newCurrent(BaseRawMessage newCurrent) {
        if (current.getExtra() == null) {
            current.setExtra(new LinkedList<>());
        }
        current.getExtra().add(newCurrent);
        current = newCurrent;
    }

    /**
     * Add message.
     * @param message message to add
     */
    protected final void add(BaseRawMessage message) {
        if (current == null) {
            current = new TextRawMessage();
            components.add(current);
        }
        if (current.getExtra() == null) {
            current.setExtra(new LinkedList<>());
        }
        current.getExtra().add(message);
        newCurrent(new TextRawMessage());
    }

    /**
     * Remove empty components from component hierarchy.
     * @param list component hierarchy
     * @return component hierarchy without empty components
     */
    private static List<BaseRawMessage> clearEmpty(List<BaseRawMessage> list) {
        if (list == null) {
            return null;
        }
        List<BaseRawMessage> result = new LinkedList<>();
        for (BaseRawMessage baseRawMessage : list) {
            baseRawMessage.setExtra(clearEmpty(baseRawMessage.getExtra()));
            if (baseRawMessage.isEmpty() && baseRawMessage.getExtra() != null) {
                for (BaseRawMessage brm : baseRawMessage.getExtra()) {
                    applyFormatting(brm, baseRawMessage);
                }
                result.addAll(baseRawMessage.getExtra());
            } else if (!baseRawMessage.isEmpty()) {
                result.add(baseRawMessage);
            }
        }
        return result.isEmpty() ? null : result;
    }

    /**
     * Apply parent's formatting to unspecified properties in child.
     * @param to component to which the formatting will be applied
     * @param from component from which the formatting will be applied
     */
    private static void applyFormatting(BaseRawMessage to, BaseRawMessage from) {
        if (to.getColor() == null) {
            to.setColor(from.getColor());
        }
        if (to.getBold() == null) {
            to.setBold(from.getBold());
        }
        if (to.getItalic() == null) {
            to.setItalic(from.getItalic());
        }
        if (to.getUnderlined() == null) {
            to.setUnderlined(from.getUnderlined());
        }
        if (to.getStrikethrough() == null) {
            to.setStrikethrough(from.getStrikethrough());
        }
        if (to.getObfuscated() == null) {
            to.setObfuscated(from.getObfuscated());
        }
        if (to.getInsertion() == null) {
            to.setInsertion(from.getInsertion());
        }
        if (to.getClickEvent() == null) {
            to.setClickEvent(from.getClickEvent());
        }
        if (to.getHoverEvent() == null) {
            to.setHoverEvent(from.getHoverEvent());
        }
    }
}
