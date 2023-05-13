package byx.util.proxy.core;

import byx.util.proxy.exception.TargetMethodException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 方法拦截器
 *
 * @author byx
 */
public interface MethodInterceptor {
    /**
     * 拦截
     *
     * @param targetMethod 目标方法
     * @return 返回值
     */
    Object intercept(TargetMethod targetMethod);

    /**
     * 执行目标方法
     *
     * @return 方法拦截器
     */
    static MethodInterceptor invokeTargetMethod() {
        return TargetMethod::invokeWithOriginalArgs;
    }

    /**
     * 拦截参数
     *
     * @param argsMapper 参数转换器
     */
    static MethodInterceptor interceptParameters(Function<Object[], Object[]> argsMapper) {
        return targetMethod -> {
            Object[] params = targetMethod.getArgs();
            return targetMethod.invoke(argsMapper.apply(params));
        };
    }

    /**
     * 拦截返回值
     *
     * @param retValMapper 返回值转换器
     */
    static MethodInterceptor interceptReturnValue(Function<Object, Object> retValMapper) {
        return targetMethod -> retValMapper.apply(targetMethod.invokeWithOriginalArgs());
    }

    /**
     * 拦截异常
     *
     * @param exceptionMapper 异常转换器
     */
    static MethodInterceptor interceptException(Function<Throwable, Object> exceptionMapper) {
        return targetMethod -> {
            try {
                return targetMethod.invokeWithOriginalArgs();
            } catch (TargetMethodException e) {
                return exceptionMapper.apply(e.getTargetException());
            }
        };
    }

    /**
     * 将目标类的部分方法委托到代理类
     *
     * @param proxy 代理类
     */
    static MethodInterceptor delegateTo(Object proxy) {
        return targetMethod -> {
            Object[] params = targetMethod.getArgs();
            Method method = targetMethod.getMethod();
            Method m = null;
            try {
                m = proxy.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
                m.setAccessible(true);
                return m.invoke(proxy, params);
            }
            // 代理对象中没有该方法，则调用目标对象方法
            catch (NoSuchMethodException | IllegalAccessException e) {
                return targetMethod.invokeWithOriginalArgs();
            }
            // 代理对象的方法抛出异常
            catch (InvocationTargetException e) {
                throw new TargetMethodException(e.getCause(), m);
            }
        };
    }

    /**
     * 指定拦截条件
     *
     * @param matcher 方法匹配器
     */
    default MethodInterceptor when(MethodMatcher matcher) {
        return targetMethod -> {
            if (matcher.match(targetMethod.getMethod())) {
                return this.intercept(targetMethod);
            }
            return targetMethod.invokeWithOriginalArgs();
        };
    }

    /**
     * 多重代理
     *
     * @param interceptor 方法拦截器
     */
    default MethodInterceptor then(MethodInterceptor interceptor) {
        return targetMethod -> interceptor.intercept(
                new TargetMethod(
                        targetMethod.getMethod(),
                        args -> this.intercept(
                                new TargetMethod(
                                        targetMethod.getMethod(),
                                        targetMethod.getInvokable(),
                                        args
                                )
                        ),
                        targetMethod.getArgs()
                )
        );
    }
}
