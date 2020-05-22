package com.ivan1pl.witchcraft.context;

/**
 * Represents a single managed object and additional data associated with it.
 */
public class ObjectData {
    /**
     * Managed object.
     */
    private final Object object;

    /**
     * Method called right after context initialization.
     */
    private final String initMethod;

    /**
     * Method called when the plugin is being disabled.
     */
    private final String shutdownMethod;

    /**
     * Constructor.
     * @param object managed object
     * @param initMethod init method
     * @param shutdownMethod shutdown method
     */
    public ObjectData(Object object, String initMethod, String shutdownMethod) {
        this.object = object;
        this.initMethod = initMethod;
        this.shutdownMethod = shutdownMethod;
    }

    /**
     * Get managed object.
     * @return managed object.
     */
    public Object getObject() {
        return object;
    }

    /**
     * Get object's init method.
     * @return init method
     */
    public String getInitMethod() {
        return initMethod;
    }

    /**
     * Get object's shutdown method.
     * @return shutdown method
     */
    public String getShutdownMethod() {
        return shutdownMethod;
    }
}
