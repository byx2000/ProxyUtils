package byx.util.proxy.test;

import org.junit.jupiter.api.Test;

import static byx.util.proxy.ProxyUtils.*;
import static byx.util.proxy.core.MethodInterceptor.*;

public class Example4 {
    public interface A {
        void f();

        void g();
    }

    public interface B {
        void f();

        void g();
    }

    public static class BImpl implements B {
        @Override
        public void f() {
            System.out.println("BImpl中的f方法");
        }

        @Override
        public void g() {
            System.out.println("BImpl中的g方法");
        }
    }

    @Test
    public void test() {
        A a = implement(A.class, delegateTo(new BImpl()));
        a.f();
        a.g();
    }
}
