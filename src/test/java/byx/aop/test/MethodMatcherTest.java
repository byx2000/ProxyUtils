package byx.aop.test;

import byx.aop.core.MethodSignature;
import org.junit.jupiter.api.Test;
import java.nio.charset.Charset;
import static byx.aop.core.MethodMatcher.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MethodMatcherTest
{
    private MethodSignature getMethod(Class<?> type, String name, Class<?>... parameterTypes)
    {
        try
        {
            return MethodSignature.of(type.getMethod(name, parameterTypes));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAll()
    {
        assertTrue(all().match(getMethod(String.class, "length")));
        assertTrue(all().match(getMethod(String.class, "substring", int.class, int.class)));
        assertTrue(all().match(getMethod(String.class, "substring", int.class, int.class)));
        assertTrue(all().match(getMethod(Integer.class, "notify")));
    }

    @Test
    public void testWithName()
    {
        assertTrue(withName("length").match(getMethod(String.class, "length")));
        assertFalse(withName("length").match(getMethod(Integer.class, "valueOf", String.class)));
    }

    @Test
    public void testWithPattern()
    {
        assertTrue(withPattern("get(.*)").match(getMethod(Object.class, "getClass")));
        assertTrue(withPattern("get(.*)").match(getMethod(String.class, "getBytes", Charset.class)));
        assertFalse(withPattern("get(.*)").match(getMethod(String.class, "length")));
        assertFalse(withPattern("get(.*)").match(getMethod(Integer.class, "valueOf", String.class)));
    }

    @Test
    public void testWithReturnType()
    {
        assertTrue(withReturnType(int.class).match(getMethod(String.class, "length")));
        assertFalse(withReturnType(Integer.class).match(getMethod(String.class, "length")));
        assertTrue(withReturnType(String.class).match(getMethod(String.class, "substring", int.class, int.class)));
        assertFalse(withReturnType(String.class).match(getMethod(Integer.class, "valueOf", String.class)));
    }

    @Test
    public void testWithParameterTypes()
    {
        assertTrue(withParameterTypes().match(getMethod(String.class, "length")));
        assertFalse(withParameterTypes().match(getMethod(String.class, "substring", int.class, int.class)));
        assertTrue(withParameterTypes(String.class).match(getMethod(Integer.class, "valueOf", String.class)));
        assertFalse(withParameterTypes(String.class).match(getMethod(String.class, "substring", int.class, int.class)));
        assertTrue(withParameterTypes(int.class, int.class).match(getMethod(String.class, "substring", int.class, int.class)));
        assertFalse(withParameterTypes(int.class, int.class).match(getMethod(Integer.class, "valueOf", String.class)));
    }

    @Test
    public void testExistInType()
    {
        assertTrue(existInType(String.class).match(getMethod(String.class, "length")));
        assertTrue(existInType(String.class).match(getMethod(String.class, "substring", int.class, int.class)));
        assertFalse(existInType(String.class).match(getMethod(Integer.class, "valueOf", String.class)));
    }

    @Test
    public void testNot()
    {
        assertFalse(withName("length").not().match(getMethod(String.class, "length")));
        assertTrue(withName("length").not().match(getMethod(String.class, "substring", int.class, int.class)));
    }

    @Test
    public void testAnnotation()
    {
        assertTrue(hasAnnotation(Test.class).match(getMethod(MethodMatcherTest.class, "testAnnotation")));
        assertFalse(hasAnnotation(Test.class).match(getMethod(String.class, "substring", int.class, int.class)));
    }
}
