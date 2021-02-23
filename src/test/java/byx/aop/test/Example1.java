package byx.aop.test;

import byx.aop.core.MethodInterceptor;
import byx.aop.core.MethodMatcher;
import byx.aop.exception.TargetMethodException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static byx.aop.AOP.proxy;
import static byx.aop.core.MethodMatcher.withPattern;

/**
 * 统一事务增强
 */
public class Example1
{
    public static class User {}

    public interface UserDao
    {
        List<User> listAll();
        void delete(int id);
        void insert(User user);
    }

    public static class UserDaoImpl implements UserDao
    {
        @Override
        public List<User> listAll()
        {
            System.out.println("正在执行listAll方法");
            return new ArrayList<>();
        }

        @Override
        public void delete(int id)
        {
            System.out.println("正在执行delete方法");
        }

        @Override
        public void insert(User user)
        {
            System.out.println("正在执行insert方法");
            throw new RuntimeException();
        }
    }

    @Test
    public void test()
    {
        // 事务增强拦截器
        MethodInterceptor transactionEnhance = (signature, targetMethod, params) ->
        {
            System.out.println("开启事务");
            try
            {
                Object ret = targetMethod.invoke(params);
                System.out.println("提交事务");
                return ret;
            }
            catch (TargetMethodException e)
            {
                System.out.println("发生异常");
                System.out.println("回滚事务");
                return null;
            }
        };

        // 配匹所有不以list开头的方法
        MethodMatcher updateMethods = withPattern("list(.*)").not();

        // 创建事务增强的UserDaoImpl
        UserDao userDao = proxy(new UserDaoImpl(), transactionEnhance.when(updateMethods));

        userDao.listAll();
        System.out.println();
        userDao.delete(1001);
        System.out.println();
        userDao.insert(new User());
    }
}
