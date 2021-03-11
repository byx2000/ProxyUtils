package byx.util.proxy.test;

import byx.util.proxy.core.MethodInterceptor;
import byx.util.proxy.core.MethodMatcher;
import org.junit.jupiter.api.Test;

import static byx.util.proxy.ProxyUtils.implement;

/**
 * 批量实现接口
 */
public class Example3 {
    public interface A {
        String f1();

        String f2();

        String f3();

        String g1();

        String g2();

        String g3();
    }

    @Test
    public void test() {
        MethodInterceptor f = (signature, targetMethod, params) -> "hello";
        MethodInterceptor g = (signature, targetMethod, params) -> "hi";

        A a = implement(A.class, f.when(MethodMatcher.withPattern("f.")).then(g.when(MethodMatcher.withPattern("g."))));

        System.out.println(a.f1());
        System.out.println(a.f2());
        System.out.println(a.f3());
        System.out.println(a.g1());
        System.out.println(a.g2());
        System.out.println(a.g3());
    }
}
