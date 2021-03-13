package byx.util.proxy.test;

import byx.util.proxy.core.*;
import byx.util.proxy.exception.TargetMethodException;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;

import static byx.util.proxy.ProxyUtils.*;

/**
 * 声明式事务管理
 */
public class Example1 {
    // 模拟JDBC的Connection
    public static class Connection {
        public void setAutoCommit(boolean flag) {
            System.out.println("setAutoCommit(" + flag + ")");
        }

        public void commit() {
            System.out.println("提交事务");
        }

        public void rollback() {
            System.out.println("回滚事务");
        }

        public void close() {
            System.out.println("关闭连接");
        }

        public void execute(String sql) {
            System.out.println("执行sql语句：" + sql);
        }
    }

    // 模拟JDBC的DataSource
    public static class DataSource {
        public Connection getConnection() {
            System.out.println("获取连接");
            return new Connection();
        }
    }

    // 声明事务管理的注解
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Inherited
    public @interface Transactional {
    }

    // 模拟连接池
    private final Connection[] connectionPool = new Connection[1];

    // User服务接口
    public interface UserService {
        void insert();

        void delete();
    }

    // User服务实现类
    public class UserServiceImpl implements UserService {
        @Override
        @Transactional
        public void insert() {
            connectionPool[0].execute("INSERT INTO ...");
        }

        @Override
        @Transactional
        public void delete() {
            connectionPool[0].execute("DELETE FROM ...");
            throw new RuntimeException("删除时抛出的异常");
        }
    }

    // 事务管理器
    public class TransactionManager implements MethodInterceptor {
        private final DataSource dataSource;

        public TransactionManager(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Object intercept(TargetMethod targetMethod) {
            Object[] params = targetMethod.getParams();
            try {
                connectionPool[0] = dataSource.getConnection();
                connectionPool[0].setAutoCommit(false);
                Object ret = targetMethod.invoke(params);
                connectionPool[0].commit();
                return ret;
            } catch (TargetMethodException e) {
                System.out.println("发生异常：" + e.getMessage());
                connectionPool[0].rollback();
                return null;
            } finally {
                connectionPool[0].close();
            }
        }
    }

    @Test
    public void test() {
        UserService userService = proxy(new UserServiceImpl(), new TransactionManager(new DataSource()).when(MethodMatcher.hasAnnotation(Transactional.class)));

        userService.insert();
        System.out.println();
        userService.delete();
    }
}
