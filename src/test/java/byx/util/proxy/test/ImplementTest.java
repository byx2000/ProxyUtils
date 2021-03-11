package byx.util.proxy.test;

import byx.util.proxy.core.MethodInterceptor;

import byx.util.proxy.exception.NotImplementedException;
import byx.util.proxy.exception.TargetMethodException;
import byx.util.proxy.core.MethodMatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static byx.util.proxy.ProxyUtils.*;
import static byx.util.proxy.core.MethodInterceptor.*;

public class ImplementTest {
    public interface Calculator {
        int add(int a, int b);

        int sub(int a, int b);

        int mul(int a, int b);
    }

    @Test
    public void test1() {
        Calculator c = implement(Calculator.class, delegateTo(new Object() {
            public int add(int a, int b) {
                return a + b;
            }

            public int sub(int a, int b) {
                return a - b;
            }
        }));

        assertEquals(5, c.add(2, 3));
        assertEquals(-4, c.sub(6, 10));
        assertThrows(NotImplementedException.class, () -> c.mul(4, 5));
    }

    @Test
    public void test2() {
        MethodInterceptor interceptor1 = (signature, targetMethod, params) -> 123;
        MethodInterceptor interceptor2 = (signature, targetMethod, params) -> 456;
        MethodInterceptor interceptor = interceptor1.when(MethodMatcher.withName("add").or(MethodMatcher.withName("sub"))).then(interceptor2.when(MethodMatcher.withName("mul")));
        Calculator c = implement(Calculator.class, interceptor);

        assertEquals(123, c.add(1, 2));
        assertEquals(123, c.sub(3, 4));
        assertEquals(456, c.mul(5, 6));
    }

    @Test
    public void test3() {
        Calculator c = implement(Calculator.class, delegateTo(new Object() {
            public int add(int a, int b) throws Exception {
                throw new Exception("故意抛出的Exception");
            }

            public int sub(int a, int b) {
                throw new RuntimeException("故意抛出的RuntimeException");
            }
        }));

        assertThrows(TargetMethodException.class, () -> c.add(1, 2));
        assertThrows(TargetMethodException.class, () -> c.sub(3, 4));
        assertThrows(NotImplementedException.class, () -> c.mul(5, 6));
    }
}
