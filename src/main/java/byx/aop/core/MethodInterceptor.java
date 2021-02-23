package byx.aop.core;

import byx.aop.exception.TargetMethodException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法拦截器
 */
public interface MethodInterceptor
{
    /**
     * 拦截
     * @param signature 方法签名
     * @param targetMethod 目标方法
     * @param params 原始参数
     * @return 返回值
     */
    Object intercept(MethodSignature signature, Invokable targetMethod, Object[] params);

    /**
     * 拦截参数
     * @param interceptor 参数拦截器
     */
    static MethodInterceptor interceptParameters(ParametersInterceptor interceptor)
    {
        return (signature, targetMethod, params) -> targetMethod.invoke(interceptor.intercept(signature, params));
    }

    /**
     * 拦截返回值
     * @param interceptor 返回值拦截器
     */
    static MethodInterceptor interceptReturnValue(ReturnValueInterceptor interceptor)
    {
        return (signature, targetMethod, params) -> interceptor.intercept(signature, targetMethod.invoke(params));
    }

    /**
     * 将目标类的部分方法委托到代理类
     * @param proxy 代理类
     */
    static MethodInterceptor delegateTo(Object proxy)
    {
        return (signature, targetMethod, params) ->
        {
            Method m = null;
            try
            {
                m = proxy.getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
                m.setAccessible(true);
                return m.invoke(proxy, params);
            }
            // 代理对象中没有该方法，则调用目标对象方法
            catch (NoSuchMethodException | IllegalAccessException e)
            {
                return targetMethod.invoke(params);
            }
            // 代理对象的方法抛出异常
            catch (InvocationTargetException e)
            {
                throw new TargetMethodException(e.getCause(), m);
            }
        };
    }

    /**
     * 指定拦截条件
     * @param matcher 方法匹配器
     */
    default MethodInterceptor when(MethodMatcher matcher)
    {
        return (signature, targetMethod, originParams) ->
        {
            if (matcher.match(signature))
                return this.intercept(signature, targetMethod, originParams);
            return targetMethod.invoke(originParams);
        };
    }

    /**
     * 嵌套代理
     * @param interceptor 方法拦截器
     */
    default MethodInterceptor then(MethodInterceptor interceptor)
    {
        return (signature, targetMethod, originParams) ->
                interceptor.intercept(signature,
                        params -> this.intercept(signature, targetMethod, params),
                        originParams);
    }
}
