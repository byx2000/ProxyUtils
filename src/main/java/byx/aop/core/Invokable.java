package byx.aop.core;

import byx.aop.exception.NotImplementedException;
import byx.aop.exception.TargetMethodException;

import java.lang.reflect.Method;

/**
 * 对一个可调用方法的封装
 */
public interface Invokable
{
    /**
     * 调用方法
     * @param params 参数
     * @return 返回值
     */
    Object invoke(Object... params);

    /**
     * 创建一个Invokable
     * @param method 方法对象
     * @param target 实例
     */
    static Invokable of(Method method, Object target)
    {
        return params ->
        {
            // 目标对象为空
            // 这种情况发生在动态实现接口时
            if (target == null)
                throw new NotImplementedException(method);
            try
            {
                method.setAccessible(true);
                return method.invoke(target, params);
            }
            // 目标对象方法抛出异常
            catch (Exception e)
            {
                throw new TargetMethodException(e, method);
            }
        };
    }
}
