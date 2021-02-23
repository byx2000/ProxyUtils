package byx.aop;

import byx.aop.core.Invokable;
import byx.aop.core.MethodInterceptor;
import byx.aop.core.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * AOP(面向切面编程)工具类
 */
public class AOP
{
    /**
     * 创建AOP代理对象
     * @param target 目标对象
     * @param interceptor 方法拦截器
     * @param <T> 返回类型
     * @return 被增强的代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxy(Object target, MethodInterceptor interceptor)
    {
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                (proxy, method, args) ->
                {
                    Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
                    return interceptor.intercept(MethodSignature.of(targetMethod), Invokable.of(targetMethod, target), args);
                });
    }

    /**
     * 实现接口
     * @param type 接口类型
     * @param interceptor 方法拦截器
     * @param <T> 返回类型
     * @return 动态生成的接口实现类
     */
    public static <T> T implement(Class<T> type, MethodInterceptor interceptor)
    {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(),
                new Class<?>[]{type},
                (proxy, method, args) -> interceptor.intercept(MethodSignature.of(method), Invokable.of(method, null), args)));
    }
}
