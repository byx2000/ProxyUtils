package byx.util.proxy.core;

import byx.util.proxy.exception.NotImplementedException;
import byx.util.proxy.exception.ProxyUtilsException;
import byx.util.proxy.exception.TargetMethodException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 对一个可调用方法的封装
 */
public interface Invokable {
    /**
     * 调用方法
     *
     * @param args 实参列表
     * @return 返回值
     */
    Object invoke(Object... args);

    /**
     * 创建一个Invokable
     *
     * @param method 方法对象
     * @param target 实例
     */
    static Invokable of(Method method, Object target) {
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
                throw new ProxyUtilsException("Cannot invoke target method: " + method, e);
            }
        };
    }
}
