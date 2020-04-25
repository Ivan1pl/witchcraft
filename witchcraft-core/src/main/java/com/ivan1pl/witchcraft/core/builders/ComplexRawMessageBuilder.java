package com.ivan1pl.witchcraft.core.builders;

import com.ivan1pl.witchcraft.core.messages.BaseRawMessage;
import com.ivan1pl.witchcraft.core.messages.TextRawMessage;
import com.ivan1pl.witchcraft.core.messages.actions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Base builder class for complex raw JSON messages.
 */
public abstract class ComplexRawMessageBuilder<T> extends BaseRawMessageBuilder<T> {
    /**
     * Add text with click action.
     * @param actionType action type
     * @param value action value
     * @return child builder for action text
     */
    public ActionMessageBuilder<T> action(ClickEvent.Action actionType, String value) {
        return new ActionMessageBuilder<>(this, new ClickEvent(actionType, value));
    }

    /**
     * Add text with item tooltip.
     * @param value item tooltip
     * @return child builder for hover text
     */
    public HoverMessageBuilder<T> itemHover(String value) {
        return new HoverMessageBuilder<>(this, new ItemHoverEvent(value));
    }

    /**
     * Add text with entity tooltip.
     * @param value entity tooltip
     * @return child builder for hover text
     */
    public HoverMessageBuilder<T> entityHover(String value) {
        return new HoverMessageBuilder<>(this, new EntityHoverEvent(value));
    }

    /**
     * Add text with raw tooltip.
     * @return child builder for raw tooltip
     */
    public HoverTooltipBuilder<T> textHover() {
        return new HoverTooltipBuilder<>(this);
    }

    /**
     * Add text with raw tooltip.
     * @param value tooltip
     * @return child builder for hover text
     */
    public HoverMessageBuilder<T> textHover(String value) {
        TextRawMessage textRawMessage = new TextRawMessage();
        textRawMessage.setText(value);
        List<BaseRawMessage> textRawMessages = new ArrayList<>();
        textRawMessages.add(textRawMessage);
        return new HoverMessageBuilder<>(this, new TextHoverEvent(textRawMessages));
    }

    /**
     * Builder class for raw JSON messages with onClick action.
     */
    public static class ActionMessageBuilder<U> extends ComplexRawMessageBuilder<ActionMessageBuilder<U>> {
        /**
         * Parent builder.
         */
        private ComplexRawMessageBuilder<U> parent;

        /**
         * Action.
         */
        private ClickEvent action;

        /**
         * Create action message builder with parent builder and action.
         * @param parent parent builder
         * @param action action
         */
        private ActionMessageBuilder(ComplexRawMessageBuilder<U> parent, ClickEvent action) {
            this.parent = parent;
            this.action = action;
        }

        /**
         * Do not use.
         */
        @Override
        public List<BaseRawMessage> build() {
            throw new UnsupportedOperationException("Use end() instead.");
        }

        /**
         * Finish building action message content and return to parent builder.
         *
         * @return parent builder
         */
        @SuppressWarnings("unchecked")
        public U end() {
            List<BaseRawMessage> result = super.build();
            TextRawMessage textRawMessage = new TextRawMessage();
            textRawMessage.setClickEvent(action);
            textRawMessage.setExtra(result);
            parent.add(textRawMessage);
            return (U) parent;
        }
    }

    /**
     * Builder class for raw JSON messages with hoverEvent.
     */
    public static class HoverMessageBuilder<U> extends ComplexRawMessageBuilder<HoverMessageBuilder<U>> {
        /**
         * Parent builder.
         */
        private ComplexRawMessageBuilder<U> parent;

        /**
         * Action.
         */
        private HoverEvent action;

        /**
         * Create hover message builder with parent builder and action.
         * @param parent parent builder
         * @param action action
         */
        private HoverMessageBuilder(ComplexRawMessageBuilder<U> parent, HoverEvent action) {
            this.parent = parent;
            this.action = action;
        }

        /**
         * Do not use.
         */
        @Override
        public List<BaseRawMessage> build() {
            throw new UnsupportedOperationException("Use end() instead.");
        }

        /**
         * Finish building hover message content and return to parent builder.
         *
         * @return parent builder
         */
        @SuppressWarnings("unchecked")
        public U end() {
            List<BaseRawMessage> result = super.build();
            TextRawMessage textRawMessage = new TextRawMessage();
            textRawMessage.setHoverEvent(action);
            textRawMessage.setExtra(result);
            parent.add(textRawMessage);
            return (U) parent;
        }
    }

    /**
     * Builder class for raw JSON message's tooltip (hoverEvent) content.
     */
    public static class HoverTooltipBuilder<U> extends BaseRawMessageBuilder<HoverTooltipBuilder<U>> {
        /**
         * Parent builder.
         */
        private ComplexRawMessageBuilder<U> parent;

        /**
         * Create hover tooltip builder with parent builder.
         * @param parent parent builder
         */
        private HoverTooltipBuilder(ComplexRawMessageBuilder<U> parent) {
            this.parent = parent;
        }

        /**
         * Do not use.
         */
        @Override
        public List<BaseRawMessage> build() {
            throw new UnsupportedOperationException("Use text() instead.");
        }

        /**
         * Finish building tooltip text and return a builder for message content.
         * @return message content builder
         */
        public HoverMessageBuilder<U> text() {
            List<BaseRawMessage> tooltip = super.build();
            return new HoverMessageBuilder<>(parent, new TextHoverEvent(tooltip));
        }
    }
}
