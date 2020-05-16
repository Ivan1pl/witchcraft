package com.ivan1pl.witchcraft.jdbc.transaction;

import com.ivan1pl.witchcraft.context.annotations.Managed;
import com.ivan1pl.witchcraft.context.proxy.Aspect;
import com.ivan1pl.witchcraft.context.proxy.InvocationCallback;
import com.ivan1pl.witchcraft.jdbc.connection.WitchCraftDataSource;
import com.ivan1pl.witchcraft.jdbc.exception.TransactionNotActiveException;
import com.ivan1pl.witchcraft.jdbc.exception.TransactionNotSupportedException;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Aspect dealing with automatic transaction management.
 */
@Managed
public class TransactionAspect implements Aspect {
    /**
     * Data source.
     */
    private final WitchCraftDataSource witchCraftDataSource;

    /**
     * Constructor.
     * @param witchCraftDataSource data source
     */
    public TransactionAspect(WitchCraftDataSource witchCraftDataSource) {
        this.witchCraftDataSource = witchCraftDataSource;
    }

    /**
     * Execute advice that should happen before method execution. This aspect does not execute any operations.
     *
     * @param self   the proxy instance
     * @param method the forwarder method for invoking the overridden method. It is null if the overridden method is
     *               abstract or declared in the interface
     * @param originalMethod overridden method
     * @param args   an array of objects containing the values of the arguments passed in the method invocation on the
     *               proxy instance. If a parameter type is a primitive type, the type of the array element is a wrapper
     *               class
     */
    @Override
    public void beforeMethod(Object self, Method method, Method originalMethod, Object[] args) {
    }

    /**
     * Execute advice that should happen after method execution. This aspect does not execute any operations.
     *
     * @param self   the proxy instance
     * @param method the forwarder method for invoking the overridden method. It is null if the overridden method is
     *               abstract or declared in the interface
     * @param originalMethod overridden method
     * @param args   an array of objects containing the values of the arguments passed in the method invocation on the
     *               proxy instance. If a parameter type is a primitive type, the type of the array element is a wrapper
     *               class
     */
    @Override
    public void afterMethod(Object self, Method method, Method originalMethod, Object[] args) {
    }

    /**
     * Execute advice that should happen around method execution. This aspect wraps the invocation with transaction
     * management logic.
     *
     * @param proceed proceed logic, containing the method for which the advice is being created and the logic of all
     *                advices with lower priority than this one.
     * @return an invocation callback to be passed as {@code proceed} method to the advice around this one
     */
    @Override
    public InvocationCallback aroundMethod(InvocationCallback proceed) {
        return (self, method, originalMethod, args) -> {
            Transactional transactional = originalMethod.getAnnotation(Transactional.class);
            if (transactional == null) {
                return proceed.apply(self, method, originalMethod, args);
            } else {
                Connection connection;
                switch (transactional.transactionMode()) {
                    case NONE:
                        return suspendTransactionAndContinue(proceed, self, method, originalMethod, args,
                                transactional.isolation());
                    case NONE_ACTIVE:
                        connection = witchCraftDataSource.getTransaction();
                        if (connection != null && !connection.getAutoCommit()) {
                            throw new TransactionNotSupportedException(
                                    "A method not supporting transactions was called inside an active transaction");
                        }
                        return proceed.apply(self, method, originalMethod, args);
                    case REQUIRED_ACTIVE:
                        connection = witchCraftDataSource.getTransaction();
                        if (connection == null || connection.getAutoCommit()) {
                            throw new TransactionNotActiveException(
                                    "A method requiring an active transaction was called but there are no active" +
                                            "transactions");
                        }
                        return proceed.apply(self, method, originalMethod, args);
                    case REQUIRED_NEW:
                        return createTransactionAndContinue(proceed, self, method, originalMethod, args,
                                transactional.isolation(), transactional.rollbackFor());
                    case REQUIRED:
                    default:
                        connection = witchCraftDataSource.getTransaction();
                        if (connection != null && !connection.getAutoCommit()) {
                            return proceed.apply(self, method, originalMethod, args);
                        }
                        return createTransactionAndContinue(proceed, self, method, originalMethod, args,
                                transactional.isolation(), transactional.rollbackFor());
                }
            }
        };
    }

    /**
     * Suspend any existing transactions and proceed.
     * @param proceed proceed logic
     * @param self the proxy instance
     * @param method the forwarder method
     * @param originalMethod overridden method
     * @param args method parameters
     * @param isolation transaction isolation
     * @return a result of invoking the wrapped method
     * @throws Throwable if unable to suspend current transaction or an exception occurs within the wrapped method
     */
    private Object suspendTransactionAndContinue(InvocationCallback proceed, Object self, Method method,
                                                 Method originalMethod, Object[] args, Isolation isolation)
            throws Throwable {
        Connection connection = witchCraftDataSource.getTransaction();
        if (connection == null) {
            return proceed.apply(self, method, originalMethod, args);
        } else {
            connection = null;
            try {
                connection = witchCraftDataSource.pushTransaction(isolation);
                connection.setAutoCommit(true);
                return proceed.apply(self, method, originalMethod, args);
            } finally {
                if (connection != null) {
                    connection = witchCraftDataSource.popTransaction();
                    if (connection instanceof TransactionConnectionWrapper) {
                        connection = ((TransactionConnectionWrapper) connection).unwrap();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                }
            }
        }
    }

    /**
     * Suspend any existing transactions, create a new one and proceed.
     * @param proceed proceed logic
     * @param self the proxy instance
     * @param method the forwarder method
     * @param originalMethod overridden method
     * @param args method parameters
     * @param isolation transaction isolation
     * @return a result of invoking the wrapped method
     * @throws Throwable if unable to suspend current transaction or create a new one or an exception occurs within the
     *                   wrapped method
     */
    private Object createTransactionAndContinue(InvocationCallback proceed, Object self, Method method,
                                                Method originalMethod, Object[] args, Isolation isolation,
                                                Class<? extends Throwable>[] rollbackFor)
            throws Throwable {
        Connection connection = null;
        try {
            connection = witchCraftDataSource.pushTransaction(isolation);
            Object result = proceed.apply(self, method, originalMethod, args);
            connection.commit();
            return result;
        } catch (Throwable t) {
            if (connection != null) {
                for (Class<? extends Throwable> toRollback : rollbackFor) {
                    if (toRollback.isInstance(t)) {
                        connection.rollback();
                        throw t;
                    }
                }
                connection.commit();
            }
            throw t;
        } finally {
            if (connection != null) {
                connection = witchCraftDataSource.popTransaction();
                if (connection instanceof TransactionConnectionWrapper) {
                    connection = ((TransactionConnectionWrapper) connection).unwrap();
                }
                if (connection != null) {
                    connection.close();
                }
            }
        }
    }

    /**
     * Get execution priority of this aspect. The lower the number, the earlier it will be executed.
     *
     * @return aspect priority
     */
    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}
