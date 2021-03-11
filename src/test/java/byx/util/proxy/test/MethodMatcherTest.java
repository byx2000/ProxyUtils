package byx.util.proxy.test;

import byx.util.proxy.core.MethodSignature;
import byx.util.proxy.core.MethodMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MethodMatcherTest {
    private MethodSignature getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        try {
            return MethodSignature.of(type.getMethod(name, parameterTypes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAll() {
        Assertions.assertTrue(MethodMatcher.all().match(getMethod(String.class, "length")));
        Assertions.assertTrue(MethodMatcher.all().match(getMethod(String.class, "substring", int.class, int.class)));
        Assertions.assertTrue(MethodMatcher.all().match(getMethod(String.class, "substring", int.class, int.class)));
        Assertions.assertTrue(MethodMatcher.all().match(getMethod(Integer.class, "notify")));
    }

    @Test
    public void testWithName() {
        Assertions.assertTrue(MethodMatcher.withName("length").match(getMethod(String.class, "length")));
        Assertions.assertFalse(MethodMatcher.withName("length").match(getMethod(Integer.class, "valueOf", String.class)));
    }

    @Test
    public void testWithPattern() {
        Assertions.assertTrue(MethodMatcher.withPattern("get(.*)").match(getMethod(Object.class, "getClass")));
        Assertions.assertTrue(MethodMatcher.withPattern("get(.*)").match(getMethod(String.class, "getBytes", Charset.class)));
        Assertions.assertFalse(MethodMatcher.withPattern("get(.*)").match(getMethod(String.class, "length")));
        Assertions.assertFalse(MethodMatcher.withPattern("get(.*)").match(getMethod(Integer.class, "valueOf", String.class)));
    }

    @Test
    public void testWithReturnType() {
        Assertions.assertTrue(MethodMatcher.withReturnType(int.class).match(getMethod(String.class, "length")));
        Assertions.assertFalse(MethodMatcher.withReturnType(Integer.class).match(getMethod(String.class, "length")));
        Assertions.assertTrue(MethodMatcher.withReturnType(String.class).match(getMethod(String.class, "substring", int.class, int.class)));
        Assertions.assertFalse(MethodMatcher.withReturnType(String.class).match(getMethod(Integer.class, "valueOf", String.class)));
    }

    @Test
    public void testWithParameterTypes() {
        Assertions.assertTrue(MethodMatcher.withParameterTypes().match(getMethod(String.class, "length")));
        Assertions.assertFalse(MethodMatcher.withParameterTypes().match(getMethod(String.class, "substring", int.class, int.class)));
        Assertions.assertTrue(MethodMatcher.withParameterTypes(String.class).match(getMethod(Integer.class, "valueOf", String.class)));
        Assertions.assertFalse(MethodMatcher.withParameterTypes(String.class).match(getMethod(String.class, "substring", int.class, int.class)));
        Assertions.assertTrue(MethodMatcher.withParameterTypes(int.class, int.class).match(getMethod(String.class, "substring", int.class, int.class)));
        Assertions.assertFalse(MethodMatcher.withParameterTypes(int.class, int.class).match(getMethod(Integer.class, "valueOf", String.class)));
    }

    @Test
    public void testExistInType() {
        Assertions.assertTrue(MethodMatcher.existInType(String.class).match(getMethod(String.class, "length")));
        Assertions.assertTrue(MethodMatcher.existInType(String.class).match(getMethod(String.class, "substring", int.class, int.class)));
        Assertions.assertFalse(MethodMatcher.existInType(String.class).match(getMethod(Integer.class, "valueOf", String.class)));
    }

    @Test
    public void testNot() {
        Assertions.assertFalse(MethodMatcher.withName("length").not().match(getMethod(String.class, "length")));
        Assertions.assertTrue(MethodMatcher.withName("length").not().match(getMethod(String.class, "substring", int.class, int.class)));
    }

    @Test
    public void testAnnotation() {
        Assertions.assertTrue(MethodMatcher.hasAnnotation(Test.class).match(getMethod(MethodMatcherTest.class, "testAnnotation")));
        Assertions.assertFalse(MethodMatcher.hasAnnotation(Test.class).match(getMethod(String.class, "substring", int.class, int.class)));
    }
}
