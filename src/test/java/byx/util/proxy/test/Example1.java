package byx.util.proxy.test;

import byx.util.proxy.ProxyUtils;
import byx.util.proxy.core.MethodInterceptor;
import byx.util.proxy.core.TargetMethod;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

import static byx.util.proxy.core.MethodMatcher.hasAnnotation;

/**
 * 模拟Spring的声明式事务@Transactional
 */
public class Example1 {
    // 模拟JDBC的Connection
    public static class Connection {
        private final int id;

        public Connection(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setAutoCommit(boolean flag) {
            System.out.println(Thread.currentThread().getName() + ": set auto commit " + flag + ": " + id);
        }

        public void execute(String sql) {
            System.out.println(Thread.currentThread().getName() + ": execute '" + sql + "' with connection: " + id);
        }

        public void commit() {
            System.out.println(Thread.currentThread().getName() + ": commit: " + id);
        }

        public void rollback() {
            System.out.println(Thread.currentThread().getName() + ": rollback: " + id);
        }

        public void close() {
            System.out.println(Thread.currentThread().getName() + ": close connection: " + id);
        }
    }

    // 模拟Spring的JdbcTemplate
    public static class JdbcTemplate {
        private static final ThreadLocal<Connection> connHolder = new ThreadLocal<>();

        public static void execute(String sql) {
            Connection conn = connHolder.get();
            if (conn == null) {
                conn = new Connection(new Random().nextInt());
                System.out.println(Thread.currentThread().getName() + ": create new connection: " + conn.getId());
                conn.execute(sql);
                conn.close();
            } else {
                conn.execute(sql);
            }
        }

        public static void startTransaction() {
            Connection conn = connHolder.get();
            if (conn == null) {
                conn = new Connection(new Random().nextInt());
                System.out.println(Thread.currentThread().getName() + ": create new connection: " + conn.getId());
                connHolder.set(conn);
            }
            conn.setAutoCommit(false);
        }

        public static void commitAndClose() {
            Connection conn = connHolder.get();
            if (conn != null) {
                conn.commit();
                conn.close();
            }
            connHolder.remove();
        }

        public static void rollbackAndClose() {
            Connection conn = connHolder.get();
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
            connHolder.remove();
        }
    }

    // 模拟Spring的Transactional注解
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Transactional {}

    // 事务增强拦截器
    public static class TransactionalInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(TargetMethod targetMethod) {
            JdbcTemplate.startTransaction();
            try {
                Object obj = targetMethod.invokeWithOriginalParams();
                JdbcTemplate.commitAndClose();
                return obj;
            } catch (Exception e) {
                JdbcTemplate.rollbackAndClose();
                return null;
            }
        }
    }

    public static class UserDao {
        public void dao1() {
            JdbcTemplate.execute("sql1");
        }

        public void dao2() {
            JdbcTemplate.execute("sql2");
        }
    }

    public static class UserService {
        private final UserDao userDao = new UserDao();

        @Transactional
        public void service1() {
            userDao.dao1();
            userDao.dao2();
        }

        @Transactional
        public void service2() {
            userDao.dao1();
            int a = 1 / 0;
            userDao.dao2();
        }

        public void service3() {
            userDao.dao1();
            userDao.dao2();
        }
    }

    @Test
    public void test() {
        UserService userService = ProxyUtils.proxy(new UserService(),
                new TransactionalInterceptor().when(hasAnnotation(Transactional.class)));

        userService.service1();
        System.out.println();
        userService.service2();
        System.out.println();
        userService.service3();
    }
}
