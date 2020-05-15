package com.ivan1pl.witchcraft.context.proxy;

import java.lang.reflect.Method;

/**
 * Callback used to wrap method invocations.
 */
@FunctionalInterface
public interface InvocationCallback {
    /**
     * Execute callback.
     * @param self the proxy instance
     * @param method the forwarder method for invoking the overridden method. It is null if the overridden method is
     *               abstract or declared in the interface
     * @param args an array of objects containing the values of the arguments passed in the method invocation on the
     *             proxy instance. If a parameter type is a primitive type, the type of the array element is a wrapper
     *             class
     * @return the resulting value of the method invocation
     * @throws Throwable if the method invocation fails
     */
    Object apply(Object self, Method method, Object[] args) throws Throwable;
}
