package byx.util.proxy.test;

import byx.util.proxy.core.MethodInterceptor;
import byx.util.proxy.core.MethodMatcher;
import byx.util.proxy.core.MethodSignature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.*;

import static byx.util.proxy.ProxyUtils.proxy;

/**
 * 参数校验
 */
public class Example5 {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Inherited
    public @interface Validate {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Inherited
    public @interface NotNull {
    }

    public interface Service {
        String login(String username, String password);
    }

    public static class ServiceImpl implements Service {
        @Validate
        public String login(@NotNull String username, @NotNull String password) {
            System.out.println("正在登录：" + username + " " + password);
            return username + " " + password;
        }
    }

    public static class User {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        @Validate
        public void setUsername(@NotNull String username) {
            System.out.println("username = " + username);
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        @Validate
        public void setPassword(@NotNull String password) {
            System.out.println("password = " + password);
            this.password = password;
        }
    }

    // 方法拦截器
    private final MethodInterceptor interceptor = targetMethod -> {
        MethodSignature signature = targetMethod.getSignature();
        Object[] params = targetMethod.getParams();
        Annotation[][] parameterAnnotations = signature.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; ++i) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof NotNull) {
                    if (params[i] == null) {
                        throw new RuntimeException("第" + (i + 1) + "个参数为null");
                    }
                }
            }
        }
        return targetMethod.invoke(params);
    };

    // 方法匹配器
    private final MethodMatcher matcher = MethodMatcher.hasAnnotation(Validate.class);

    @Test
    public void test1() {
        Service service = proxy(new ServiceImpl(), interceptor.when(matcher));

        assertEquals("admin 123456", service.login("admin", "123456"));
        assertThrows(RuntimeException.class, () -> service.login(null, "123456"));
        assertThrows(RuntimeException.class, () -> service.login("admin", null));
        assertThrows(RuntimeException.class, () -> service.login(null, null));
    }

    @Test
    public void test2() {
        User user = proxy(new User(), interceptor.when(matcher));

        user.setUsername("XiaoMing");
        assertEquals("XiaoMing", user.getUsername());
        assertThrows(RuntimeException.class, () -> user.setUsername(null));
        assertEquals("XiaoMing", user.getUsername());

        user.setPassword("123456");
        assertEquals("123456", user.getPassword());
        assertThrows(RuntimeException.class, () -> user.setPassword(null));
        assertEquals("123456", user.getPassword());
    }
}
