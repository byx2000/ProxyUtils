package byx.util.proxy.test;

import byx.util.proxy.core.MethodInterceptor;
import byx.util.proxy.core.MethodMatcher;
import org.junit.jupiter.api.Test;

import static byx.util.proxy.ProxyUtils.proxy;
import static org.junit.jupiter.api.Assertions.*;

public class CglibTest {
    public static class Student {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public interface User {
        String getUsername();

        String getPassword();
    }

    public static final class UserImpl implements User {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @Test
    public void test1() {
        int[] id = new int[]{0};
        String[] name = new String[]{""};
        boolean[] flag = new boolean[]{false};
        Student s = proxy(new Student(), (signature, targetMethod, params) -> {
            flag[0] = true;
            assertEquals(1, params.length);
            if (params[0] instanceof Integer) {
                id[0] = (int) params[0];
            } else if (params[0] instanceof String) {
                name[0] = (String) params[0];
            }
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            System.out.println("结束拦截" + signature.getName() + "方法");
            return ret;
        });

        s.setId(1001);
        assertTrue(flag[0]);
        assertEquals(1001, id[0]);

        flag[0] = false;
        s.setName("XiaoMing");
        assertTrue(flag[0]);
        assertEquals("XiaoMing", name[0]);
    }

    @Test
    public void test2() {
        boolean[] flag = new boolean[]{false};
        Student s = proxy(new Student(), ((MethodInterceptor) (signature, targetMethod, params) -> {
            flag[0] = true;
            assertEquals(1, params.length);
            assertEquals(1001, params[0]);
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            System.out.println("结束拦截" + signature.getName() + "方法");
            return ret;
        }).when(MethodMatcher.withName("setId")));

        s.setId(1001);
        assertTrue(flag[0]);

        flag[0] = false;
        s.setName("XiaoMing");
        assertFalse(flag[0]);
    }

    @Test
    public void test3() {
        boolean[] flag = new boolean[]{false};
        Student s = proxy(new Student(), ((MethodInterceptor) (signature, targetMethod, params) -> {
            flag[0] = true;
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            assertEquals("byx", ret);
            System.out.println("结束拦截" + signature.getName() + "方法");
            return "XiaoMing";
        }).when(MethodMatcher.withPattern("get(.*)").andReturnType(String.class)));

        s.setName("byx");
        assertFalse(flag[0]);

        String name = s.getName();
        assertTrue(flag[0]);
        assertEquals("XiaoMing", name);
    }

    @Test
    public void test4() {
        boolean[] flag = new boolean[]{false};
        User user = proxy(new UserImpl(), (signature, targetMethod, params) -> {
            flag[0] = true;
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            System.out.println("结束拦截" + signature.getName() + "方法");
            return ret;
        });

        user.getUsername();
        assertTrue(flag[0]);
    }
}
