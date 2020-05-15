package com.ivan1pl.witchcraft.commands.base;

import java.lang.reflect.Method;

/**
 * Class used to hold subcommand methods and their proxy versions.
 */
class MethodHolder {
    /**
     * Proxy method.
     */
    private final Method proxyMethod;

    /**
     * Original method.
     */
    private final Method originalMethod;

    /**
     * Constructor.
     * @param proxyMethod proxy method
     * @param originalMethod original method
     */
    MethodHolder(Method proxyMethod, Method originalMethod) {
        this.proxyMethod = proxyMethod;
        this.originalMethod = originalMethod;
    }

    /**
     * Get proxy method.
     * @return proxy method
     */
    public Method getProxyMethod() {
        return proxyMethod;
    }

    /**
     * Get original method.
     * @return original method
     */
    public Method getOriginalMethod() {
        return originalMethod;
    }
}
