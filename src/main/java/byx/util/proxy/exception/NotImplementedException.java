package byx.util.proxy.exception;

import java.lang.reflect.Method;

/**
 * 方法未实现
 */
public class NotImplementedException extends RuntimeException {
    public NotImplementedException(Method method) {
        super("Not implemented method: " + method);
    }
}
