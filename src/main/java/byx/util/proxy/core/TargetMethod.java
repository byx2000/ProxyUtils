package byx.util.proxy.core;

import java.lang.reflect.Method;

/**
 * 目标方法
 */
public class TargetMethod {
    private final Method method;
    private final Invokable invokable;
    private final Object[] args;

    /**
     * 创建目标方法
     *
     * @param method 方法对象
     * @param invokable 调用器
     * @param args    实参列表
     */
    public TargetMethod(Method method, Invokable invokable, Object[] args) {
        this.method = method;
        this.invokable = invokable;
        this.args = args;
    }

    /**
     * 获取方法调用器
     *
     * @return 调用器
     */
    public Invokable getInvokable() {
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
        return invokable.invoke(args);
    }

    /**
     * 使用特定参数调用原始方法
     *
     * @param args 参数
     * @return 返回值
     */
    public Object invoke(Object... args) {
        return invokable.invoke(args);
    }

    /**
     * 获取Method对象
     * @return Method对象
     */
    public Method getMethod() {
        return method;
    }
}
