package com.ivan1pl.witchcraft.core.messages;

/**
 * Represents NBT chat component object.
 */
public class NbtRawMessage extends BaseRawMessage {
    /**
     * A string indicating the NBT path used for looking up NBT values from an entity, a block entity or a command
     * storage. Ignored when any of the previous fields exist in the root object.
     */
    private String nbt;

    /**
     * A boolean to indicate whether the game should interpret the SNBT value at the path indicated by {@link #nbt} as a
     * raw JSON text (according to this raw JSON text structure).
     */
    private boolean interpret;

    /**
     * A string specifying the coordinates of the block entity from which the NBT value is obtained. The coordinates can
     * be absolute or relative.
     */
    private String block;

    /**
     * A string specifying the target selector for the entity from which the NBT value is obtained.
     */
    private String entity;

    /**
     * A string specifying the namespaced ID of the command storage from which the NBT value is obtained.
     */
    private String storage;

    /**
     * Get nbt.
     * @return nbt
     */
    public String getNbt() {
        return nbt;
    }

    /**
     * Set nbt.
     * <p>
     * A string indicating the NBT path used for looking up NBT values from an entity, a block entity or a command
     * storage. Ignored when any of the previous fields exist in the root object.
     *
     * @param nbt nbt to set
     */
    public void setNbt(String nbt) {
        this.nbt = nbt;
    }

    /**
     * Get interpret modifier.
     * @return interpret modifier
     */
    public boolean isInterpret() {
        return interpret;
    }

    /**
     * Set interpret modifier.
     * <p>
     * A boolean to indicate whether the game should interpret the SNBT value at the path indicated by {@link #nbt} as a
     * raw JSON text (according to this raw JSON text structure).
     *
     * @param interpret interpret modifier to set
     */
    public void setInterpret(boolean interpret) {
        this.interpret = interpret;
    }

    /**
     * Get block.
     * @return block
     */
    public String getBlock() {
        return block;
    }

    /**
     * Set block.
     * <p>
     * A string specifying the coordinates of the block entity from which the NBT value is obtained. The coordinates can
     * be absolute or relative.
     *
     * @param block block to set
     */
    public void setBlock(String block) {
        this.block = block;
    }

    /**
     * Get entity.
     * @return entity
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Set entity.
     * <p>
     * A string specifying the target selector for the entity from which the NBT value is obtained.
     *
     * @param entity entity to set
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * Get storage.
     * @return storage
     */
    public String getStorage() {
        return storage;
    }

    /**
     * Set storage.
     * <p>
     * A string specifying the namespaced ID of the command storage from which the NBT value is obtained.
     *
     * @param storage storage to set
     */
    public void setStorage(String storage) {
        this.storage = storage;
    }

    /**
     * Check if this nbt chat component's content is empty.
     * @return {@code true} iff message content is empty
     */
    @Override
    public boolean isEmpty() {
        return nbt == null || nbt.isEmpty();
    }

    /**
     * Create new instance with formatting copied from other component.
     * @param from component to copy from
     * @return new component instance
     */
    public static NbtRawMessage from(BaseRawMessage from) {
        NbtRawMessage nbtRawMessage = new NbtRawMessage();
        nbtRawMessage.copyFormattingFrom(from);
        return nbtRawMessage;
    }
}
