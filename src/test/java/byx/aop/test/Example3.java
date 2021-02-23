package byx.aop.test;

import byx.aop.core.MethodInterceptor;
import org.junit.jupiter.api.Test;

import static byx.aop.AOP.implement;
import static byx.aop.core.MethodMatcher.withPattern;

/**
 * 批量实现接口
 */
public class Example3
{
    public interface A
    {
        String f1();
        String f2();
        String f3();
        String g1();
        String g2();
        String g3();
    }

    @Test
    public void test()
    {
        MethodInterceptor f = (signature, targetMethod, params) -> "hello";
        MethodInterceptor g = (signature, targetMethod, params) -> "hi";

        A a = implement(A.class,
                f.when(withPattern("f.")).then(g.when(withPattern("g."))));

        System.out.println(a.f1());
        System.out.println(a.f2());
        System.out.println(a.f3());
        System.out.println(a.g1());
        System.out.println(a.g2());
        System.out.println(a.g3());
    }
}
