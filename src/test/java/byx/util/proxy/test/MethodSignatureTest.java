package byx.util.proxy.test;

import byx.util.proxy.core.MethodSignature;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MethodSignatureTest {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface AnnotationOnMethod {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface AnnotationOnParam {
        int value();
    }

    public static class A {
        @AnnotationOnMethod("hello")
        public boolean fun(int i, @AnnotationOnParam(123) Double d, String s) {
            return false;
        }

        public List<String> foo() {
            return null;
        }
    }

    @Test
    public void test() throws NoSuchMethodException {
        MethodSignature signature = MethodSignature.of(A.class.getMethod("fun", int.class, Double.class, String.class));

        assertEquals("fun", signature.getName());
        assertEquals(boolean.class, signature.getReturnType());
        assertArrayEquals(new Class<?>[]{int.class, Double.class, String.class}, signature.getParameterTypes());

        AnnotationOnMethod annotationOnMethod = signature.getAnnotation(AnnotationOnMethod.class);
        assertNotNull(annotationOnMethod);
        assertEquals("hello", annotationOnMethod.value());

        assertNull(signature.getAnnotation(Test.class));

        Annotation[] annotations = signature.getAnnotations();
        assertEquals(1, annotations.length);
        assertTrue(annotations[0] instanceof AnnotationOnMethod);
        assertEquals("hello", ((AnnotationOnMethod) annotations[0]).value());

        assertTrue(signature.hasAnnotation(AnnotationOnMethod.class));

        Annotation[][] parameterAnnotations = signature.getParameterAnnotations();
        assertEquals(3, parameterAnnotations.length);
        assertEquals(0, parameterAnnotations[0].length);
        assertEquals(1, parameterAnnotations[1].length);
        assertEquals(0, parameterAnnotations[2].length);
        assertTrue(parameterAnnotations[1][0] instanceof AnnotationOnParam);
        assertEquals(123, ((AnnotationOnParam) parameterAnnotations[1][0]).value());

        MethodSignature signature2 = MethodSignature.of(A.class.getMethod("foo"));
        Type type = signature2.getGenericReturnType();
        assertEquals(String.class, ((ParameterizedType) type).getActualTypeArguments()[0]);
        type = signature.getGenericReturnType();
        assertEquals(boolean.class, type);

        String[] names = signature.getParameterNames();
        assertTrue(Arrays.equals(new String[]{"i", "d", "s"}, names));

        names = signature2.getParameterNames();
        assertEquals(0, names.length);

        assertTrue(signature.isPublic());
        assertFalse(signature.isPrivate());
        assertFalse(signature.isProtected());
    }
}
