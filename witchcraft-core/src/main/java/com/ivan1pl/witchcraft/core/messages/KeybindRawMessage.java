package com.ivan1pl.witchcraft.core.messages;

/**
 * Represents keybind chat component object.
 */
public class KeybindRawMessage extends BaseRawMessage {
    /**
     * A string that can be used to display the key needed to perform a certain action. An example is
     * {@code key.inventory} which always displays "E" unless the player has set a different key for opening their
     * inventory.
     */
    private Keybind keybind;

    /**
     * Get keybind.
     * @return keybind
     */
    public Keybind getKeybind() {
        return keybind;
    }

    /**
     * Set keybind.
     *
     * A string that can be used to display the key needed to perform a certain action. An example is
     * {@code key.inventory} which always displays "E" unless the player has set a different key for opening their
     * inventory.
     *
     * @param keybind keybind to set
     */
    public void setKeybind(Keybind keybind) {
        this.keybind = keybind;
    }

    /**
     * Check if this keybind chat component's content is empty.
     * @return {@code true} iff message content is empty
     */
    @Override
    public boolean isEmpty() {
        return keybind == null;
    }

    /**
     * Create new instance with formatting copied from other component.
     * @param from component to copy from
     * @return new component instance
     */
    public static KeybindRawMessage from(BaseRawMessage from) {
        KeybindRawMessage keybindRawMessage = new KeybindRawMessage();
        keybindRawMessage.copyFormattingFrom(from);
        return keybindRawMessage;
    }
}
