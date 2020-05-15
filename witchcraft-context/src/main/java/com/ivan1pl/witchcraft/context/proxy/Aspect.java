package com.ivan1pl.witchcraft.context.proxy;

import java.lang.reflect.Method;

/**
 * A single aspect containing {@code before}, {@code around} and {@code after} advices.
 */
public interface Aspect {
    /**
     * Execute advice that should happen before method execution.
     * @param self the proxy instance
     * @param method the forwarder method for invoking the overridden method. It is null if the overridden method is
     *               abstract or declared in the interface
     * @param args an array of objects containing the values of the arguments passed in the method invocation on the
     *             proxy instance. If a parameter type is a primitive type, the type of the array element is a wrapper
     *             class
     * @throws Throwable if the advice fails
     */
    void beforeMethod(Object self, Method method, Object[] args) throws Throwable;

    /**
     * Execute advice that should happen after method execution.
     * @param self the proxy instance
     * @param method the forwarder method for invoking the overridden method. It is null if the overridden method is
     *               abstract or declared in the interface
     * @param args an array of objects containing the values of the arguments passed in the method invocation on the
     *             proxy instance. If a parameter type is a primitive type, the type of the array element is a wrapper
     *             class
     * @throws Throwable if the advice fails
     */
    void afterMethod(Object self, Method method, Object[] args) throws Throwable;

    /**
     * Execute advice that should happen around method execution.
     * @param proceed proceed logic, containing the method for which the advice is being created and the logic of all
     *                advices with lower priority than this one.
     * @return an invocation callback to be passed as {@code proceed} method to the advice around this one
     * @throws Throwable if the advice or method execution fails
     */
    InvocationCallback aroundMethod(InvocationCallback proceed) throws Throwable;

    /**
     * Get execution priority of this aspect. The lower the number, the earlier it will be executed.
     * @return aspect priority
     */
    default int getPriority() {
        return 0;
    }
}
