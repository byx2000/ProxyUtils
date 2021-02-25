package byx.aop;

import byx.aop.core.Invokable;
import byx.aop.core.MethodInterceptor;
import byx.aop.core.MethodSignature;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    public static <T> T proxy(Object target, MethodInterceptor interceptor)
    {
        // final类使用jdk代理
        if (Modifier.isFinal(target.getClass().getModifiers()))
            return proxyByJdk(target, interceptor);
        // 没有接口的类使用cglib代理
        else if (target.getClass().getInterfaces().length == 0)
            return proxyByCglib(target, interceptor);
        // 其他情况使用jdk代理
        else
            return proxyByJdk(target, interceptor);
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

    /**
     * 使用jdk动态代理
     */
    @SuppressWarnings("unchecked")
    private static <T> T proxyByJdk(Object target, MethodInterceptor interceptor)
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
     * 使用cglib动态代理
     */
    @SuppressWarnings("unchecked")
    private static <T> T proxyByCglib(Object target, MethodInterceptor interceptor)
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback((InvocationHandler) (proxy, method, args) ->
                interceptor.intercept(MethodSignature.of(method), Invokable.of(method, target), args));
        return (T) enhancer.create();
    }

    /**
     * 关闭cglib的警告
     */
    private static void disableAccessWarnings()
    {
        try
        {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);

            Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

            Class<?> loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        }
        catch (Exception ignored) {}
    }

    static
    {
        disableAccessWarnings();
    }
}
