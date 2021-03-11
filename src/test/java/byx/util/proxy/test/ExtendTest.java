package byx.util.proxy.test;

import byx.util.proxy.exception.NotImplementedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static byx.util.proxy.ProxyUtils.*;
import static byx.util.proxy.core.MethodInterceptor.*;

public class ExtendTest {
    public static abstract class User {
        public abstract void setUsername(String username);

        public abstract String getUsername();

        public abstract void setPassword(String password);

        public abstract String getPassword();
    }

    public static class Student {
        public int getId() {
            return 1001;
        }

        public String getName() {
            return "XiaoMing";
        }
    }

    @Test
    public void test1() {
        User user = extend(User.class, delegateTo(new Object() {
            private String username;

            public void setUsername(String username) {
                this.username = username;
            }

            public String getUsername() {
                return username;
            }
        }));

        user.setUsername("XiaoMing");
        assertEquals("XiaoMing", user.getUsername());
        assertThrows(NotImplementedException.class, () -> user.setPassword("123456"));
        assertThrows(NotImplementedException.class, user::getPassword);
    }

    @Test
    public void test2() {
        Student student = extend(Student.class, delegateTo(new Object() {
            public int getId() {
                return 2002;
            }
        }));

        assertEquals(2002, student.getId());
        assertThrows(NotImplementedException.class, student::getName);
    }
}
