package byx.util.proxy.exception;

import java.lang.reflect.Method;

/**
 * 方法未实现
 *
 * @author byx
 */
public class NotImplementedException extends ProxyException {
    public NotImplementedException(Method method) {
        super("Not implemented method: " + method);
    }
}
