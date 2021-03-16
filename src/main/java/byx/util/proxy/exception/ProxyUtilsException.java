package byx.util.proxy.exception;

/**
 * ProxyUtils异常基类
 *
 * @author byx
 */
public class ProxyUtilsException extends RuntimeException {
    public ProxyUtilsException(String msg) {
        super(msg);
    }

    public ProxyUtilsException(String msg, Throwable e) {
        super(msg, e);
    }
}
