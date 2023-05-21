package byx.util.proxy.core;

import byx.util.proxy.exception.NotImplementedException;
import byx.util.proxy.exception.ProxyException;
import byx.util.proxy.exception.TargetMethodException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 封装目标方法
 */
public class TargetMethod {
    private final Method method;
    private final Object[] args;
    private final Function<Object[], Object> invokable;

    public TargetMethod(Method method, Object[] args, Object target) {
        this(method, args, createInvokable(method, target));
    }

    public TargetMethod(Method method, Object[] args, Function<Object[], Object> invokable) {
        this.method = method;
        this.args = args;
        this.invokable = invokable;
    }

    /**
     * 获取方法调用器
     *
     * @return 调用器
     */
    public Function<Object[], Object> getInvokable() {
        return invokable;
    }

    /**
     * 获取实参列表
     *
     * @return 实参列表
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * 使用原始参数调用目标方法
     *
     * @return 返回值
     */
    public Object invokeWithOriginalArgs() {
        return invokable.apply(args);
    }

    /**
     * 使用特定参数调用原始方法
     *
     * @param args 参数
     * @return 返回值
     */
    public Object invoke(Object... args) {
        return invokable.apply(args);
    }

    /**
     * 获取Method对象
     * @return Method对象
     */
    public Method getMethod() {
        return method;
    }

    // 封装目标方法的调用逻辑
    private static Function<Object[], Object> createInvokable(Method method, Object target) {
        return args -> {
            // 目标对象为空
            // 这种情况发生在动态实现接口时
            if (target == null) {
                throw new NotImplementedException(method);
            }
            try {
                method.setAccessible(true);
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw new TargetMethodException(e.getTargetException(), method);
            } catch (IllegalAccessException e) {
                throw new ProxyException("Cannot invoke target method: " + method, e);
            }
        };
    }
}
