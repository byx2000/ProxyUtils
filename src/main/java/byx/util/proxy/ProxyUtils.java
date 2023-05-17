package byx.util.proxy;

import byx.util.proxy.core.Invokable;
import byx.util.proxy.core.MethodInterceptor;
import byx.util.proxy.core.TargetMethod;
import byx.util.proxy.exception.ProxyException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * 动态代理工具类
 *
 * @author byx
 */
public class ProxyUtils {
    /**
     * 创建代理对象
     * 自动选择代理方式
     *
     * @param target      目标对象
     * @param interceptor 方法拦截器
     * @return 代理对象
     */
    public static <T> T proxy(Object target, MethodInterceptor interceptor) {
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null.");
        }

        // final类使用jdk代理
        if (Modifier.isFinal(target.getClass().getModifiers())) {
            return proxyByJdk(target.getClass().getInterfaces(), target, interceptor);
        }
        // 没有接口的类使用ByteBuddy代理
        else if (target.getClass().getInterfaces().length == 0) {
            return proxyByByteBuddy(target.getClass(), target, interceptor);
        }
        // 其他情况使用jdk代理
        else {
            return proxyByJdk(target.getClass().getInterfaces(), target, interceptor);
        }
    }

    /**
     * 动态实现接口
     *
     * @param interfaceType 接口类型
     * @param interceptor   方法拦截器
     * @param <T>           返回类型
     * @return 动态生成的接口实现类
     */
    public static <T> T implement(Class<T> interfaceType, MethodInterceptor interceptor) {
        return proxyByJdk(new Class[]{interfaceType}, null, interceptor);
    }

    /**
     * 动态生成子类
     *
     * @param superClass  父类
     * @param interceptor 方法拦截器
     * @param <T>         返回类型
     * @return 动态生成的子类
     */
    public static <T> T extend(Class<T> superClass, MethodInterceptor interceptor) {
        return proxyByByteBuddy(superClass, null, interceptor);
    }

    /**
     * 使用jdk生成代理类
     *
     * @param target 目标对象
     * @param interceptor 方法拦截器
     * @return 代理对象
     */
    public static <T> T proxyByJdk(Object target, MethodInterceptor interceptor) {
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null.");
        }
        return proxyByJdk(target.getClass().getInterfaces(), target, interceptor);
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxyByJdk(Class<?>[] interfaceTypes, Object target, MethodInterceptor interceptor) {
        return (T) Proxy.newProxyInstance(
                ProxyUtils.class.getClassLoader(),
                interfaceTypes,
                (proxy, method, args) -> {
                    Method targetMethod = method;
                    if (target != null) {
                        targetMethod = target.getClass()
                                .getMethod(method.getName(), method.getParameterTypes());
                    }
                    return interceptor.intercept(new TargetMethod(targetMethod, Invokable.of(targetMethod, target), args));
                });
    }

    /**
     * 使用ByteBuddy生成代理类
     *
     * @param target 目标对象
     * @param interceptor 方法拦截器
     * @return 代理对象
     */
    public static <T> T proxyByByteBuddy(Object target, MethodInterceptor interceptor) {
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null.");
        }
        return proxyByByteBuddy(target.getClass(), target, interceptor);
    }

    private static <T> T proxyByByteBuddy(Class<?> superclass, Object target, MethodInterceptor interceptor) {
        try {
            return (T) new ByteBuddy()
                    .subclass(superclass)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(new ByteBuddyInterceptor(target, interceptor)))
                    .make()
                    .load(ProxyUtils.class.getClassLoader())
                    .getLoaded()
                    .getConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new ProxyException("Cannot proxy by ByteBuddy: " + e);
        }
    }

    /**
     * ByteBuddy方法拦截器
     */
    protected static class ByteBuddyInterceptor {
        private final Object target;
        private final MethodInterceptor interceptor;

        private ByteBuddyInterceptor(Object target, MethodInterceptor interceptor) {
            this.target = target;
            this.interceptor = interceptor;
        }

        @RuntimeType
        public Object intercept(@Origin Method method, @AllArguments Object[] args) {
            return interceptor.intercept(new TargetMethod(method, Invokable.of(method, target), args));
        }
    }
}
