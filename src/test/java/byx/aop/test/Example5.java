package byx.aop.test;

import byx.aop.core.MethodInterceptor;
import byx.aop.core.MethodMatcher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.*;

import static byx.aop.AOP.proxy;
import static byx.aop.core.MethodMatcher.hasAnnotation;

public class Example5
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Inherited
    public @interface Validate
    {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Inherited
    public @interface NotNull
    {}

    public interface Service
    {
        void login(String username, String password);
    }

    public static class ServiceImpl implements Service
    {
        @Validate
        public void login(@NotNull  String username, @NotNull String password)
        {
            System.out.println("正在登录：" + username + " " + password);
        }
    }

    @Test
    public void test()
    {
        MethodInterceptor interceptor = (signature, targetMethod, params) ->
        {
            Annotation[][] parameterAnnotations = signature.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; ++i)
            {
                for (Annotation annotation : parameterAnnotations[i])
                {
                    if (annotation instanceof NotNull)
                    {
                        if (params[i] == null)
                            throw new RuntimeException("第" + (i + 1) + "个参数为null");
                    }
                }
            }
            return targetMethod.invoke(params);
        };

        MethodMatcher matcher = hasAnnotation(Validate.class);

        Service service = proxy(new ServiceImpl(), interceptor.when(matcher));

        service.login("admin", "123456");
        assertThrows(RuntimeException.class, () -> service.login(null, "123456"));
        assertThrows(RuntimeException.class, () -> service.login("admin", null));
        assertThrows(RuntimeException.class, () -> service.login(null, null));
    }
}
