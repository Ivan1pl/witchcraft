package com.ivan1pl.witchcraft.context.proxy;

import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Invocation handler for managed classes. All non-aspect classes will use this handler to execute their methods.
 */
public class ProxyInvocationHandler implements MethodHandler {
    /**
     * All aspects in the priority order.
     */
    private final PriorityQueue<Aspect> aspects =
            new PriorityQueue<>(11, Comparator.comparingInt(Aspect::getPriority));

    /**
     * Add aspect.
     * @param aspect aspect to add
     */
    public void addAspect(Aspect aspect) {
        aspects.add(aspect);
    }

    /**
     * Is called when a method is invoked on a proxy instance associated with this handler. This method must process
     * that method invocation.
     * @param self the proxy instance
     * @param thisMethod the overridden method declared in the super class or interface
     * @param proceed the forwarder method for invoking the overridden method. It is null if the overridden method is
     *                abstract or declared in the interface
     * @param args an array of objects containing the values of the arguments passed in the method invocation on the
     *             proxy instance. If a parameter type is a primitive type, the type of the array element is a wrapper
     *             class
     * @return the resulting value of the method invocation
     * @throws Throwable if the method invocation fails
     */
    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        beforeMethod(self, proceed, args);
        Object result = aroundMethod(self, proceed, args);
        afterMethod(self, proceed, args);
        return result;
    }

    /**
     * Execute all advices that should happen before method execution.
     * @param self the proxy instance
     * @param method the forwarder method for invoking the overridden method. It is null if the overridden method is
     *               abstract or declared in the interface
     * @param args an array of objects containing the values of the arguments passed in the method invocation on the
     *             proxy instance. If a parameter type is a primitive type, the type of the array element is a wrapper
     *             class
     * @throws Throwable if any advice fails
     */
    private void beforeMethod(Object self, Method method, Object[] args) throws Throwable {
        for (Aspect aspect : aspects) {
            aspect.beforeMethod(self, method, args);
        }
    }

    /**
     * Execute all advices that should happen after method execution.
     * @param self the proxy instance
     * @param method the forwarder method for invoking the overridden method. It is null if the overridden method is
     *               abstract or declared in the interface
     * @param args an array of objects containing the values of the arguments passed in the method invocation on the
     *             proxy instance. If a parameter type is a primitive type, the type of the array element is a wrapper
     *             class
     * @throws Throwable if any advice fails
     */
    private void afterMethod(Object self, Method method, Object[] args) throws Throwable {
        for (Aspect aspect : aspects) {
            aspect.afterMethod(self, method, args);
        }
    }

    /**
     * Execute all advices that should happen around method execution.
     * @param self the proxy instance
     * @param method the forwarder method for invoking the overridden method. It is null if the overridden method is
     *               abstract or declared in the interface
     * @param args an array of objects containing the values of the arguments passed in the method invocation on the
     *             proxy instance. If a parameter type is a primitive type, the type of the array element is a wrapper
     *             class
     * @return the resulting value of the method invocation
     * @throws Throwable if any advice fails
     */
    private Object aroundMethod(Object self, Method method, Object[] args)
            throws Throwable {
        InvocationCallback proceed = (selfObj, proceedMethod, methodArgs) ->
                proceedMethod.invoke(selfObj, methodArgs);
        Aspect[] aspects = this.aspects.toArray(new Aspect[0]);
        for (int i = aspects.length - 1; i >= 0; --i) {
            proceed = aspects[i].aroundMethod(proceed);
        }
        return proceed.apply(self, method, args);
    }
}
