# ByxAOP——简易AOP框架

ByxAOP是一个基于JDK动态代理和Cglib动态代理的简易AOP框架，具有以下功能特性：

* 对目标对象的特定方法进行拦截和增强
* 支持灵活的拦截规则和自定义拦截规则
* 根据目标对象自动选择代理方式（JDK动态代理或者Cglib动态代理）
* 动态实现接口和批量实现接口方法
* 灵活的对象委托机制

## 在项目中引入ByxAOP

1. 添加maven仓库地址

    ```xml
    <repositories>
        <repository>
            <id>byx-maven-repo</id>
            <name>byx-maven-repo</name>
            <url>https://gitee.com/byx2000/maven-repo/raw/master/</url>
        </repository>
    </repositories>
    ```

2. 添加maven依赖

    ```xml
    <dependencies>
        <dependency>
            <groupId>byx.aop</groupId>
            <artifactId>ByxAOP</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
    ```

## 使用前准备

在使用ByxAOP前，建议先在源文件中静态导入下列包：

```java
import static byx.aop.AOP.*;
import static byx.aop.core.MethodInterceptor.*;
import static byx.aop.core.MethodMatcher.*;
```

## 使用示例

通过一个简单例子来快速了解ByxAOP。

### UserDao接口

```java
public interface UserDao
{
    int listAll();
    int listById(int id);
    void deleteByName(String name);
}
```

### UserDaoImpl实现类

```java
public class UserDaoImpl implements UserDao
{
    @Override
    public int listAll()
    {
        System.out.println("正在执行listAll方法");
        return 123;
    }

    @Override
    public int listById(int id)
    {
        System.out.println("正在执行listById方法：id = " + id);
        return 456;
    }

    @Override
    public void deleteByName(String name)
    {
        System.out.println("正在执行deleteByName方法：name = " + name);
    }
}
```

### 主函数

```java
public static void main(String[] args)
{
    // 定义方法拦截器
    MethodInterceptor interceptor = (signature, targetMethod, params) ->
    {
        System.out.println("开始拦截" + signature.getName() + "方法");
        System.out.println("原始参数：" + Arrays.toString(params));
        Object ret = targetMethod.invoke(params);
        System.out.println("原始返回值：" + ret);
        System.out.println("结束拦截" + signature.getName() + "方法");
        return ret;
    };

    // 定义方法匹配器：匹配所有方法名以list开头且返回值为int类型的方法
    MethodMatcher matcher = withPattern("list(.*)").andReturnType(int.class);

    // 创建AOP代理对象
    UserDao userDao = proxy(new UserDaoImpl(), interceptor.when(matcher));

    userDao.listAll();
    System.out.println();
    userDao.listById(1001);
    System.out.println();
    userDao.deleteByName("XiaoMing");
}
```

### 输出结果

```
开始拦截listAll方法
原始参数：null
正在执行listAll方法
原始返回值：123
结束拦截listAll方法

开始拦截listById方法
原始参数：[1001]
正在执行listById方法：id = 1001
原始返回值：456
结束拦截listById方法

正在执行deleteByName方法：name = XiaoMing
```

从输出结果可以看到，`UserDaoImpl`中的`listAll`方法和`listById`方法都被增强了，而`deleteByName`方法没有被增强。

## API文档

[API文档](./doc/API.md)

## 更多使用示例

* [参数校验](./doc/参数校验.md)
* [声明式事务管理](./doc/声明式事务管理.md)
* [动态实现接口](./doc/动态实现接口.md)
* [批量实现接口方法](./doc/批量实现接口方法.md)
* [优雅地创建适配器](./doc/优雅地创建适配器.md)