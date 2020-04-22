package com.ivan1pl.witchcraft.core.messages;

import com.google.gson.annotations.SerializedName;

/**
 * Enumeration of all possible keybinds.
 */
public enum Keybind {
    @SerializedName("key.jump")    JUMP("key.jump"),
    @SerializedName("key.sneak")   SNEAK("key.sneak"),
    @SerializedName("key.sprint")  SPRINT("key.sprint"),
    @SerializedName("key.left")    LEFT("key.left"),
    @SerializedName("key.right")   RIGHT("key.right"),
    @SerializedName("key.back")    BACK("key.back"),
    @SerializedName("key.forward") FORWARD("key.forward"),

    @SerializedName("key.attack")   ATTACK("key.attack"),
    @SerializedName("key.pickItem") PICK_ITEM("key.pickItem"),
    @SerializedName("key.use")      USE("key.use"),

    @SerializedName("key.drop")      DROP("key.drop"),
    @SerializedName("key.hotbar.1")  HOTBAR_1("key.hotbar.1"),
    @SerializedName("key.hotbar.2")  HOTBAR_2("key.hotbar.2"),
    @SerializedName("key.hotbar.3")  HOTBAR_3("key.hotbar.3"),
    @SerializedName("key.hotbar.4")  HOTBAR_4("key.hotbar.4"),
    @SerializedName("key.hotbar.5")  HOTBAR_5("key.hotbar.5"),
    @SerializedName("key.hotbar.6")  HOTBAR_6("key.hotbar.6"),
    @SerializedName("key.hotbar.7")  HOTBAR_7("key.hotbar.7"),
    @SerializedName("key.hotbar.8")  HOTBAR_8("key.hotbar.8"),
    @SerializedName("key.hotbar.9")  HOTBAR_9("key.hotbar.9"),
    @SerializedName("key.inventory") INVENTORY("key.inventory"),
    @SerializedName("key.swapHands") SWAP_HANDS("key.swapHands"),

    @SerializedName("key.loadToolbarActivator") LOAD_TOOLBAR_ACTIVATOR("key.loadToolbarActivator"),
    @SerializedName("key.saveToolbarActivator") SAVE_TOOLBAR_ACTIVATOR("key.saveToolbarActivator"),

    @SerializedName("key.playerlist") PLAYERLIST("key.playerlist"),
    @SerializedName("key.chat")       CHAT("key.chat"),
    @SerializedName("key.command")    COMMAND("key.command"),

    @SerializedName("key.advancements")      ADVANCEMENTS("key.advancements"),
    @SerializedName("key.spectatorOutlines") SPECTATOR_OUTLINES("key.spectatorOutlines"),
    @SerializedName("key.screenshot")        SCREENSHOT("key.screenshot"),
    @SerializedName("key.smoothCamera")      SMOOTH_CAMERA("key.smoothCamera"),
    @SerializedName("key.fullscreen")        FULLSCREEN("key.fullscreen"),
    @SerializedName("key.togglePerspective") TOGGLE_PERSPECTIVE("key.togglePerspective"),
    ;

    /**
     * Keybind key.
     */
    private String key;

    Keybind(String key) {
        this.key = key;
    }

    /**
     * Get keybind key.
     * @return keybind key
     */
    public String getKey() {
        return key;
    }
}
