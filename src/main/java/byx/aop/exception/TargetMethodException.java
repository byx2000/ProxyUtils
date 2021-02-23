package byx.aop.exception;

import java.lang.reflect.Method;

/**
 * 目标方法产生的异常
 */
public class TargetMethodException extends RuntimeException
{
    public TargetMethodException(Throwable e, Method method)
    {
        super("Exception from target method: " + method, e);
    }
}
