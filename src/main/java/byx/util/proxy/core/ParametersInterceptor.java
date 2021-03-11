package byx.util.proxy.core;

/**
 * 方法参数拦截器
 */
public interface ParametersInterceptor {
    /**
     * 拦截
     *
     * @param signature 方法签名
     * @param params    原始参数数组
     * @return 增强后的参数数组
     */
    Object[] intercept(MethodSignature signature, Object[] params);
}
