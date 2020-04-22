package com.ivan1pl.witchcraft.core.messages;

import java.util.List;

/**
 * Represents translation chat component object.
 */
public class TranslationRawMessage extends BaseRawMessage {
    /**
     * The translation identifier of text to be displayed using the player's selected language. This identifier is the
     * same as the identifiers found in lang files from assets or resource packs.
     */
    private String translate;

    /**
     * A list of chat component arguments and/or string arguments to be used by {@link #translate}.
     *
     * The arguments are text corresponding to the arguments used by the translation string in the current language, in
     * order (for example, the first list element corresponds to "%1$s" in a translation string). Argument structure
     * repeats this raw JSON text structure.
     */
    private List<BaseRawMessage> with;

    /**
     * Get translate.
     * @return translate
     */
    public String getTranslate() {
        return translate;
    }

    /**
     * Set translate.
     *
     * The translation identifier of text to be displayed using the player's selected language. This identifier is the
     * same as the identifiers found in lang files from assets or resource packs.
     *
     * @param translate translate to set
     */
    public void setTranslate(String translate) {
        this.translate = translate;
    }

    /**
     * Get translate arguments.
     * @return translate arguments
     */
    public List<BaseRawMessage> getWith() {
        return with;
    }

    /**
     * Set translate arguments.
     *
     * A list of chat component arguments and/or string arguments to be used by {@link #translate}.
     *
     * The arguments are text corresponding to the arguments used by the translation string in the current language, in
     * order (for example, the first list element corresponds to "%1$s" in a translation string). Argument structure
     * repeats this raw JSON text structure.
     *
     * @param with translate arguments to set
     */
    public void setWith(List<BaseRawMessage> with) {
        this.with = with;
    }

    /**
     * Check if this translation chat component's content is empty.
     * @return {@code true} iff message content is empty
     */
    @Override
    public boolean isEmpty() {
        return translate == null || translate.isEmpty();
    }

    /**
     * Create new instance with formatting copied from other component.
     * @param from component to copy from
     * @return new component instance
     */
    public static TranslationRawMessage from(BaseRawMessage from) {
        TranslationRawMessage translationRawMessage = new TranslationRawMessage();
        translationRawMessage.copyFormattingFrom(from);
        return translationRawMessage;
    }
}
