package byx.util.proxy.test;

import byx.util.proxy.core.*;
import byx.util.proxy.exception.TargetMethodException;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static byx.util.proxy.ProxyUtils.proxy;
import static byx.util.proxy.core.MethodInterceptor.*;
import static byx.util.proxy.core.MethodMatcher.*;
import static org.junit.jupiter.api.Assertions.*;

public class MethodInterceptorTest {
    public interface A {
        void f1();

        void f2(int i, String s);

        int f2();

        String f3(int i, String s);
    }

    public static class AImpl implements A {
        @Override
        public void f1() {

        }

        @Override
        public void f2(int i, String s) {

        }

        @Override
        public int f2() {
            return 123;
        }

        @Override
        public String f3(int i, String s) {
            return s + " " + i;
        }
    }

    @Test
    public void testInterceptor() {
        boolean[] flag = new boolean[]{false};
        MethodInterceptor interceptor = targetMethod -> {
            flag[0] = true;
            return targetMethod.invokeWithOriginalArgs();
        };
        A a = proxy(new AImpl(), interceptor);

        a.f1();
        assertTrue(flag[0]);

        flag[0] = false;
        a.f2(123, "hello");
        assertTrue(flag[0]);

        flag[0] = false;
        a.f2();
        assertTrue(flag[0]);

        flag[0] = false;
        a.f3(123, "hello");
        assertTrue(flag[0]);
    }

    @Test
    public void testWhen() {
        boolean[] flag = new boolean[]{false};
        MethodInterceptor interceptor = targetMethod -> {
            flag[0] = true;
            return targetMethod.invokeWithOriginalArgs();
        };
        MethodMatcher matcher = MethodMatcher.withName("f2");
        A a = proxy(new AImpl(), interceptor.when(matcher));

        a.f1();
        assertFalse(flag[0]);

        a.f3(123, "hello");
        assertFalse(flag[0]);

        a.f2();
        assertTrue(flag[0]);

        flag[0] = false;
        a.f2(123, "hello");
        assertTrue(flag[0]);
    }

    @Test
    public void testThen() {
        String[] s = new String[]{""};
        MethodInterceptor interceptor1 = targetMethod -> {
            s[0] += "1b";
            Object ret = targetMethod.invokeWithOriginalArgs();
            s[0] += "1e";
            return ret;
        };
        MethodInterceptor interceptor2 = targetMethod -> {
            s[0] += "2b";
            Object ret = targetMethod.invokeWithOriginalArgs();
            s[0] += "2e";
            return ret;
        };
        A a = proxy(new AImpl(), interceptor1.then(interceptor2));

        a.f1();
        assertEquals("2b1b1e2e", s[0]);
    }

    @Test
    public void testWhenAndThen() {
        String[] s = new String[]{"", ""};
        MethodInterceptor interceptor1 = targetMethod -> {
            s[0] += "a";
            return targetMethod.invokeWithOriginalArgs();
        };
        MethodMatcher matcher1 = MethodMatcher.withName("f1");
        MethodInterceptor interceptor2 = targetMethod -> {
            s[1] += "b";
            return targetMethod.invokeWithOriginalArgs();
        };
        MethodMatcher matcher2 = MethodMatcher.withName("f3");
        A a = proxy(new AImpl(), interceptor1.when(matcher1).then(interceptor2.when(matcher2)));

        a.f2();
        assertEquals("", s[0]);
        assertEquals("", s[1]);

        a.f2(123, "hello");
        assertEquals("", s[0]);
        assertEquals("", s[1]);

        a.f1();
        assertEquals("a", s[0]);
        assertEquals("", s[1]);

        a.f3(123, "hello");
        assertEquals("a", s[0]);
        assertEquals("b", s[1]);
    }

    @Test
    public void testInterceptParameters() {
        boolean[] flag = new boolean[]{false};
        Function<Object[], Object[]> argsMapper = args -> {
            flag[0] = true;
            assertEquals(args.length, 2);
            assertTrue(args[0] instanceof Integer);
            assertTrue(args[1] instanceof String);
            assertEquals(123, args[0]);
            assertEquals("hi", args[1]);
            return new Object[]{456, "abc"};
        };
        A a = proxy(new AImpl(), interceptParameters(argsMapper).when(MethodMatcher.withName("f3")));

        String ret = a.f3(123, "hi");
        assertTrue(flag[0]);
        assertEquals("abc 456", ret);
    }

    @Test
    public void testInterceptReturnValue() {
        boolean[] flag = new boolean[]{false};
        Function<Object, Object> retValMapper = retVal -> {
            flag[0] = true;
            assertTrue(retVal instanceof Integer);
            assertEquals(123, retVal);
            return 456;
        };
        A a = proxy(new AImpl(), interceptReturnValue(retValMapper).when(MethodMatcher.withName("f2").andReturnType(int.class)));

        int ret = a.f2();
        assertTrue(flag[0]);
        assertEquals(456, ret);
    }

    @Test
    public void testDelegateToProxy() {
        A a = proxy(new AImpl(), delegateTo(new Object() {
            public String f3(int i, String s) {
                return s + " " + i + "!";
            }
        }));

        assertEquals("hello 123!", a.f3(123, "hello"));
        assertEquals(123, a.f2());
    }

    @Test
    public void testException1() {
        A a = proxy(new AImpl(), delegateTo(new Object() {
            public void f1() throws Exception {
                throw new Exception("故意抛出的Exception");
            }

            public String f3(int i, String s) {
                throw new RuntimeException("故意抛出的RuntimeException");
            }
        }));

        assertThrows(TargetMethodException.class, a::f1);
        assertThrows(TargetMethodException.class, () -> a.f3(123, "hello"));
    }

    @Test
    public void testException2() {
        A a = proxy(new AImpl(), delegateTo(new Object() {
            public void f1() throws Exception {
                throw new Exception("故意抛出的Exception");
            }

            public void f2(int i, String s) {
                throw new RuntimeException("故意抛出的RuntimeException");
            }
        }));

        assertThrows(TargetMethodException.class, a::f1);
        assertThrows(TargetMethodException.class, () -> a.f2(123, "hello"));
    }

    @Test
    public void testException3() {
        A a1 = new A() {
            @Override
            public void f1() {

            }

            @Override
            public void f2(int i, String s) {
                throw new RuntimeException("故意抛出的RuntimeException");
            }

            @Override
            public int f2() {
                return 0;
            }

            @Override
            public String f3(int i, String s) {
                return null;
            }
        };
        A a2 = proxy(a1, targetMethod -> targetMethod.invokeWithOriginalArgs());

        assertThrows(TargetMethodException.class, () -> a2.f2(123, "hello"));
    }

    @Test
    public void testInvokeTargetMethod() {
        A a = proxy(new AImpl(), invokeTargetMethod());
        assertEquals(123, a.f2());
        assertEquals("abc 100", a.f3(100, "abc"));
    }

    public static class MyException1 extends Exception {
    }

    public static class MyException2 extends RuntimeException {
    }

    public static class B {
        public int f1() throws Exception {
            throw new MyException1();
        }

        public String f2() {
            throw new MyException2();
        }
    }

    @Test
    public void testExceptionIntercept() throws Exception {
        B b = proxy(new B(), interceptException(e -> {
            assertTrue(e instanceof MyException1);
            return 1001;
        }).when(withName("f1")).then(interceptException(e -> {
            assertTrue(e instanceof MyException2);
            return "exception";
        }).when(withName("f2"))));

        assertEquals(1001, b.f1());
        assertEquals("exception", b.f2());
    }
}
