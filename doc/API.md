# API文档

## AOP类

该类是实现AOP功能的工具方法。

### proxy方法

该方法用于创建AOP代理类。

#### 方法签名

```java
public static <T> T proxy(Object target, MethodInterceptor interceptor);
```

#### 参数

|参数|说明|
|---|---|
|`target`|目标类（被增强的类）|
|`interceptor`|方法拦截器|

#### 行为

对于`target`中的每个方法，都会被`interceptor`拦截。

* 当目标对象没有实现任何接口时，使用Cglib动态代理
* 其他情况则使用JDK动态代理

### implement方法

该方法用于动态实现接口。

#### 方法签名

```java
public static <T> T implement(Class<T> interfaceType, MethodInterceptor interceptor);
```

#### 参数

|参数|说明|
|---|---|
|`interfaceType`|接口类型|
|`interceptor`|方法拦截器|

#### 行为

对于`interfaceType`接口中的每个方法，都会被`interceptor`拦截。

### extend方法

该方法用于动态创建子类

#### 方法签名

```java
public static <T> T extend(Class<T> parentType, MethodInterceptor interceptor);
```

#### 参数

|参数|说明|
|---|---|
|`parentType`|父类|
|`interceptor`|方法拦截器|

#### 行为

对于`parentType`中的每个方法，都会被`interceptor`拦截。

## MethodInterceptor接口

该接口用于封装方法的拦截过程。

### 定义

```java
public interface MethodInterceptor
{
    Object intercept(MethodSignature signature, Invokable targetMethod, Object[] params);
}
```

### 说明

当目标方法被拦截时，对应的`MethodInterceptor`的`intercept`方法会被调用。

|参数|说明|
|---|---|
|`signature`|目标方法签名，用于在拦截时获取目标方法信息|
|`targetMethod`|目标方法调用器，用于在拦截时调用目标方法|
|`params`|传递给目标方法的参数|

`intercept`方法返回目标方法增强后的返回值。

### 实现类

该接口有很多预定义的实现类，全部都是通过`MethodInterceptor`的静态工厂方法来获取：

|工厂方法|说明|
|---|---|
|`when`|指定拦截器的拦截条件|
|`then`|嵌套拦截|
|`delegateTo`|将目标方法代理到另一个对象的相同签名方法|
|`interceptParameters`|参数拦截器|
|`interceptReturnValue`|返回值拦截器|

## MethodSignature接口

该接口用于封装目标方法的签名。

### 说明

该接口的所有方法说明如下：

|方法|说明|
|---|---|
|`getName`|获取方法名|
|`getReturnType`|获取返回值类型|
|`getParameterTypes`|获取参数类型|
|`getAnnotation`|获取方法的指定注解|
|`getAnnotation`|获取方法上的指定注解|
|`getAnnotations`|获取方法上的所有注解|
|`hasAnnotation`|方法是否被某个注解标注|
|`getParameterAnnotations`|获取方法参数上的注解|
|`isPublic`|是否为public方法|
|`isPrivate`|是否为private方法|
|`isProtected`|是否为protected方法|

## Invokable接口

该接口封装了一个可调用的方法。

### 定义

```java
public interface Invokable
{
    Object invoke(Object... params);
}
```

### 说明

通过调用`Invokable`的`invoke`方法就可以间接调用它所封装方法。

`params`是传递给封装方法的参数。

`invoke`为封装方法的返回值。

## MethodMatcher接口

该接口用于表示对特定方法的匹配，用于创建方法拦截器时指定拦截条件。

### 定义

```java
public interface MethodMatcher
{
    boolean match(MethodSignature signature);
}
```

### 实现类

该接口有很多预定义的实现类，全部都是通过`MethodMatcher`的静态工厂方法来获取：

|工厂方法|说明|
|---|---|
|`all`|匹配所有方法|
|`withName`|匹配指定名称的方法|
|`withPattern`|匹配方法名具有特定模式的方法|
|`withReturnType`|匹配具有特定返回值的方法|
|`withParameterTypes`|匹配具有指定参数类型的方法|
|`existInType`|匹配存在于另一个类型中的方法|
|`hasAnnotation`|匹配被指定注解标注的方法|
|`and`|匹配同时满足两个匹配条件的方法|
|`or`|匹配至少满足两个匹配条件其中之一的方法|
|`not`|匹配不满足指定匹配结果的方法|

## ParametersInterceptor接口

该接口用于对目标方法的参数进行增强。

### 定义

```java
public interface ParametersInterceptor
{
    Object[] intercept(MethodSignature signature, Object[] params);
}
```

### 说明

`signature`是目标方法签名。

`params`是原始参数。

`intercept`方法返回增强后的参数。

## ReturnValueInterceptor接口

该接口用于对目标方法的返回值进行增强。

### 定义

```java
public interface ReturnValueInterceptor
{
    Object intercept(MethodSignature signature, Object returnValue);
}
```

### 说明

`signature`是目标方法签名。

`returnValue`是目标方法的原始返回值。

`intercept`方法返回增强后的返回值。