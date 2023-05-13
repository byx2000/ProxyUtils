# ProxyUtils——Java动态代理工具类

ProxyUtils是一个动态代理工具类，基于JDK动态代理和CGLIB动态代理，并对这两种动态代理API进行了封装，让使用更方便。

ProxyUtils提供以下功能特性：

* 灵活的方法拦截器配置
* 灵活的方法匹配器配置
* 丰富的预定义方法拦截器和方法匹配器
* 动态创建接口实现类
* 动态创建子类
* 根据目标对象自动选择代理类型（JDK或CGLIB）

## 在项目中引入ProxyUtils

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
            <groupId>byx.util</groupId>
            <artifactId>ProxyUtils</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
    ```

## API文档

[API文档](http://byx2000.gitee.io/javadoc/ByxAOP-1.0.0-javadoc/)

## 使用示例

通过一个简单例子来快速了解ProxyUtils。

`UserDao`接口：

```java
public interface UserDao {
    int listAll();
    int listById(int id);
    void deleteByName(String name);
}
```

`UserDaoImpl`实现类：

```java
public class UserDaoImpl implements UserDao {
    @Override
    public int listAll() {
        System.out.println("正在执行listAll方法");
        return 123;
    }

    @Override
    public int listById(int id) {
        System.out.println("正在执行listById方法：id = " + id);
        return 456;
    }

    @Override
    public void deleteByName(String name) {
        System.out.println("正在执行deleteByName方法：name = " + name);
    }
}
```

主函数：

```java
import byx.util.proxy.ProxyUtils;
import byx.util.proxy.core.MethodInterceptor;
import byx.util.proxy.core.MethodMatcher;

import java.util.Arrays;

import static byx.util.proxy.core.MethodMatcher.*;

public class Main {
   public static void main(String[] args) {
      // 定义方法拦截器
      MethodInterceptor interceptor = targetMethod -> {
         MethodSignature signature = targetMethod.getSignature();
         Object[] params = targetMethod.getArgs();
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
      UserDao userDao = ProxyUtils.proxy(new UserDaoImpl(), interceptor.when(matcher));

      userDao.listAll();
      System.out.println();
      userDao.listById(1001);
      System.out.println();
      userDao.deleteByName("XiaoMing");
   }
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

## 更多使用示例

* [参数校验](./doc/参数校验.md)
* [声明式事务管理](./doc/声明式事务管理.md)
* [动态实现接口](./doc/动态实现接口.md)
* [批量实现接口方法](./doc/批量实现接口方法.md)
* [优雅地创建适配器](./doc/优雅地创建适配器.md)