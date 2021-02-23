package byx.aop.test;

import byx.aop.exception.NotImplementedException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static byx.aop.AOP.*;
import static byx.aop.core.MethodInterceptor.*;

/**
 * 动态实现接口
 */
public class Example2
{
    public interface Calculator
    {
        int add(int a, int b);
        int sub(int a, int b);
        int mul(int a, int b);
    }

    @Test
    public void test()
    {
        Calculator calculator = implement(Calculator.class, delegateTo(new Object()
        {
            public int add(int a, int b)
            {
                return a + b;
            }

            public int sub(int a, int b)
            {
                return a - b;
            }
        }));

        System.out.println(calculator.add(1, 2));
        System.out.println(calculator.sub(3, 4));
        assertThrows(NotImplementedException.class, () -> calculator.mul(5, 6));
    }
}
