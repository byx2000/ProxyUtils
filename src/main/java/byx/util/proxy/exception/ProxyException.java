package byx.util.proxy.exception;

/**
 * ProxyUtils异常基类
 *
 * @author byx
 */
public class ProxyException extends RuntimeException {
    public ProxyException(String msg) {
        super(msg);
    }

    public ProxyException(String msg, Throwable e) {
        super(msg, e);
    }
}
