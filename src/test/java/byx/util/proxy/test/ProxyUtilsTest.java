package byx.util.proxy.test;

import byx.util.proxy.core.Invokable;
import byx.util.proxy.core.MethodInterceptor;
import byx.util.proxy.core.MethodMatcher;
import byx.util.proxy.core.MethodSignature;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static byx.util.proxy.ProxyUtils.proxy;
import static org.junit.jupiter.api.Assertions.*;

public class ProxyUtilsTest {
    public static class MyInterceptor implements MethodInterceptor {
        private final String name;

        public MyInterceptor(String name) {
            this.name = name;
        }

        @Override
        public Object intercept(MethodSignature signature, Invokable targetMethod, Object[] params) {
            System.out.println(name + ": 开始拦截" + signature.getName() + "方法");
            System.out.println(name + ": 原始参数：" + Arrays.toString(params));
            Object ret = targetMethod.invoke(params[0] + " " + name);
            System.out.println(name + ": 原始返回值：" + ret);
            System.out.println(name + ": 结束拦截" + signature.getName() + "方法");
            return ret + " " + name;
        }
    }

    private final MyInterceptor interceptor1 = new MyInterceptor("interceptor1");
    private final MyInterceptor interceptor2 = new MyInterceptor("interceptor2");
    private final MyInterceptor interceptor3 = new MyInterceptor("interceptor3");
    private final MyInterceptor interceptor4 = new MyInterceptor("interceptor4");

    @Test
    public void test1() {
        UserDao userDao = proxy(new UserDaoImpl(), interceptor1.when(MethodMatcher.withParameterTypes(String.class)));

        userDao.checkListAllParameter(s -> assertEquals("listAll的参数 interceptor1", s));
        userDao.checkListByIdParameter(s -> assertEquals("listById的参数 interceptor1", s));
        userDao.checkInsertParameter(s -> assertEquals("insert的参数 interceptor1", s));
        userDao.checkDeleteParameter(s -> assertEquals("delete的参数 interceptor1", s));

        assertEquals("listAll的返回值 interceptor1", userDao.listAll("listAll的参数"));
        assertEquals("listById的返回值 interceptor1", userDao.listById("listById的参数"));
        assertEquals("insert的返回值 interceptor1", userDao.insert("insert的参数"));
        assertEquals("delete的返回值 interceptor1", userDao.delete("delete的参数"));
    }

    @Test
    public void test2() {
        UserDao userDao = proxy(new UserDaoImpl(), interceptor1.when(MethodMatcher.withName("listAll")));

        userDao.checkListAllParameter(s -> assertEquals("listAll的参数 interceptor1", s));
        userDao.checkListByIdParameter(s -> assertEquals("listById的参数", s));
        userDao.checkInsertParameter(s -> assertEquals("insert的参数", s));
        userDao.checkDeleteParameter(s -> assertEquals("delete的参数", s));

        assertEquals("listAll的返回值 interceptor1", userDao.listAll("listAll的参数"));
        assertEquals("listById的返回值", userDao.listById("listById的参数"));
        assertEquals("insert的返回值", userDao.insert("insert的参数"));
        assertEquals("delete的返回值", userDao.delete("delete的参数"));
    }

    @Test
    public void test3() {
        UserDao userDao = proxy(new UserDaoImpl(), interceptor1.when(MethodMatcher.withName("listAll").or(MethodMatcher.withName("insert"))));

        userDao.checkListAllParameter(s -> assertEquals("listAll的参数 interceptor1", s));
        userDao.checkListByIdParameter(s -> assertEquals("listById的参数", s));
        userDao.checkInsertParameter(s -> assertEquals("insert的参数 interceptor1", s));
        userDao.checkDeleteParameter(s -> assertEquals("delete的参数", s));

        assertEquals("listAll的返回值 interceptor1", userDao.listAll("listAll的参数"));
        assertEquals("listById的返回值", userDao.listById("listById的参数"));
        assertEquals("insert的返回值 interceptor1", userDao.insert("insert的参数"));
        assertEquals("delete的返回值", userDao.delete("delete的参数"));
    }

    @Test
    public void test4() {
        UserDao userDao = proxy(new UserDaoImpl(), interceptor1.then(interceptor2).when(MethodMatcher.withParameterTypes(String.class)));

        userDao.checkListAllParameter(s -> assertEquals("listAll的参数 interceptor2 interceptor1", s));
        userDao.checkListByIdParameter(s -> assertEquals("listById的参数 interceptor2 interceptor1", s));
        userDao.checkInsertParameter(s -> assertEquals("insert的参数 interceptor2 interceptor1", s));
        userDao.checkDeleteParameter(s -> assertEquals("delete的参数 interceptor2 interceptor1", s));

        assertEquals("listAll的返回值 interceptor1 interceptor2", userDao.listAll("listAll的参数"));
        assertEquals("listById的返回值 interceptor1 interceptor2", userDao.listById("listById的参数"));
        assertEquals("insert的返回值 interceptor1 interceptor2", userDao.insert("insert的参数"));
        assertEquals("delete的返回值 interceptor1 interceptor2", userDao.delete("delete的参数"));
    }

    @Test
    public void test5() {
        UserDao userDao = proxy(new UserDaoImpl(), interceptor1.when(MethodMatcher.withName("listById")).then(interceptor2.when(MethodMatcher.withName("delete"))));

        userDao.checkListAllParameter(s -> assertEquals("listAll的参数", s));
        userDao.checkListByIdParameter(s -> assertEquals("listById的参数 interceptor1", s));
        userDao.checkInsertParameter(s -> assertEquals("insert的参数", s));
        userDao.checkDeleteParameter(s -> assertEquals("delete的参数 interceptor2", s));

        assertEquals("listAll的返回值", userDao.listAll("listAll的参数"));
        assertEquals("listById的返回值 interceptor1", userDao.listById("listById的参数"));
        assertEquals("insert的返回值", userDao.insert("insert的参数"));
        assertEquals("delete的返回值 interceptor2", userDao.delete("delete的参数"));
    }

    @Test
    public void test6() {
        final boolean[] flags = {false, false};
        MethodInterceptor interceptor = (signature, targetMethod, params) -> {
            flags[0] = true;
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            assertNull(ret);
            System.out.println("结束拦截" + signature.getName() + "方法");
            flags[1] = true;
            return ret;
        };
        MethodMatcher matcher = MethodMatcher.withName("f1").andParameterTypes(int.class, String.class).andReturnType(void.class);
        MyClass myClass = proxy(new MyClassImpl(), interceptor.when(matcher));

        myClass.f1(100, "hello");
        assertTrue(flags[0]);
        assertTrue(flags[1]);
    }

    @Test
    public void test7() {
        final boolean[] flags = {false, false};
        MethodInterceptor interceptor = (signature, targetMethod, params) -> {
            flags[0] = true;
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            assertNull(ret);
            System.out.println("结束拦截" + signature.getName() + "方法");
            flags[1] = true;
            return ret;
        };
        MethodMatcher matcher = MethodMatcher.withReturnType(void.class).andName("f2").andParameterTypes();
        MyClass myClass = proxy(new MyClassImpl(), interceptor.when(matcher));

        myClass.f2("hello");
        assertFalse(flags[0]);
        assertFalse(flags[1]);

        myClass.f2();
        assertTrue(flags[0]);
        assertTrue(flags[1]);
    }

    @Test
    public void test8() {
        final boolean[] flags = {false, false};
        MethodInterceptor interceptor = (signature, targetMethod, params) -> {
            flags[0] = true;
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            assertEquals("0", ret);
            System.out.println("结束拦截" + signature.getName() + "方法");
            flags[1] = true;
            return ret;
        };
        MethodMatcher matcher = MethodMatcher.withParameterTypes(int.class, String.class).andReturnType(String.class).andName("f3");
        MyClass myClass = proxy(new MyClassImpl(), interceptor.when(matcher));

        myClass.f3("hello");
        assertFalse(flags[0]);
        assertFalse(flags[1]);

        myClass.f3(123, "hello");
        assertTrue(flags[0]);
        assertTrue(flags[1]);
    }

    @Test
    public void test9() {
        final boolean[] flags = {false, false};
        MethodInterceptor interceptor = (signature, targetMethod, params) -> {
            flags[0] = true;
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            System.out.println("结束拦截" + signature.getName() + "方法");
            flags[1] = true;
            return ret;
        };
        MethodMatcher matcher = MethodMatcher.withName("f2");
        MyClass myClass = proxy(new MyClassImpl(), interceptor.when(matcher));

        myClass.f2();
        assertTrue(flags[0]);
        assertTrue(flags[1]);

        flags[0] = flags[1] = false;

        myClass.f2("hello");
        assertTrue(flags[0]);
        assertTrue(flags[1]);

        flags[0] = flags[1] = false;

        myClass.f3("hello");
        assertFalse(flags[0]);
        assertFalse(flags[1]);
    }

    @Test
    public void test10() {
        final boolean[] flags = {false, false};
        MethodInterceptor interceptor = (signature, targetMethod, params) -> {
            flags[0] = true;
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            System.out.println("结束拦截" + signature.getName() + "方法");
            flags[1] = true;
            return ret;
        };
        MethodMatcher matcher = MethodMatcher.withPattern("(f1)|(f4)");
        MyClass myClass = proxy(new MyClassImpl(), interceptor.when(matcher));

        myClass.f1(123, "hello");
        assertTrue(flags[0]);
        assertTrue(flags[1]);

        flags[0] = flags[1] = false;

        myClass.f4();
        assertTrue(flags[0]);
        assertTrue(flags[1]);

        flags[0] = flags[1] = false;

        myClass.f3("hello");
        assertFalse(flags[0]);
        assertFalse(flags[1]);
    }

    @Test
    public void test11() {
        final boolean[] flags = {false, false};
        MethodInterceptor interceptor = (signature, targetMethod, params) -> {
            flags[0] = true;
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            System.out.println("结束拦截" + signature.getName() + "方法");
            flags[1] = true;
            return ret;
        };
        MethodMatcher matcher = MethodMatcher.withReturnType(String.class).andPattern("f.");
        MyClass myClass = proxy(new MyClassImpl(), interceptor.when(matcher));

        myClass.f3(123, "hello");
        assertTrue(flags[0]);
        assertTrue(flags[1]);

        flags[0] = flags[1] = false;

        myClass.f4();
        assertTrue(flags[0]);
        assertTrue(flags[1]);

        flags[0] = flags[1] = false;

        myClass.f1(123, "hello");
        assertFalse(flags[0]);
        assertFalse(flags[1]);
    }
}
