# SpringMVC概述
Web程序三层架构
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/9c7f526678604a70bea376a95be214b0.png)

数据层：Mybatis
表现层：SpringMVC
## SpringMVC入门案例
SpringMVC制作过程：
1. 创建web工程（Maven结构）
2. 设置tomcat服务器，加载web工程（tomcat）
3. 导入坐标（SpringMVC+Servlet）
4. 定义处理请求的功能类（UserController）
5. 设置请求映射（配置映射关系）
6. 将SpringMVC设定加载到Tomcat容器

具体代码见[https://github.com/cug-lucifer/springmvc_01_quickstart](https://github.com/cug-lucifer/springmvc_01_quickstart)
**@Controller**
类注解，位于SpringMVC控制类定义上方，设定SpringMVC核心控制器bean
```java
@Controller
public class UserController{}
```

**@RequestMapping**
方法注解，位于SpringMVC控制器方法定义上方，设置当前控制器方法请求访问路径
**@ResponseBody**
方法注解，位于SpringMVC控制器方法定义上方，设置当前控制器方法响应内容为当前返回值，无需解析
```java
@RequestMapping("/save")
@ResponseBody
public String save(){
	System.out.println("saving ... ");
	return "{'module':'SpringMVC}";
}
```
**AbstractDispatcherServletInitializer**
SpringMVC提供的快速初始化Web3.0容器的抽象类
```java

public class ServletContainerInitConfig extends AbstractDispatcherServletInitializer {

    // 加载springMVC容器配置
    @Override
    protected WebApplicationContext createServletApplicationContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(SpringMvcConfig.class);
        return ctx;
    }

    //设置哪些请求归属springMVC处理
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    //加载spring容器配置
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        return null;
    }
}

```
### 启动服务器初始化过程
1. 服务器启动，执行`ServletContainersInitConfig`类，初始化web容器
2. 执行`createServletApplicationContext`方法，创建WebApplicationContext对象
3. 加载`SpringMvcConfig`
4. 执行`@ComponentScan`加载对应的bean
5. 加载`UserController`，每个`@RequestMapping`的名称对应一个具体的方法
6. 执行`getServletMappings`方法，定义所有的请求都通过SpringMVC

### 单次请求过程
1. 发送请求`localhost/save`
2. web容器发现所有请求都经过SpringMVC，将请求交给SpringMVC处理
3. 解析请求路径 `/save`
4. 由`/save`匹配执行对应的方法`save()`
5. 执行`save()`
6. 检测到有`@ResponseBody`直接将`save()`方法的返回值作为响应报文体返回给请求方

## Controller加载控制与业务bean加载控制
- SpringMVC相关bean（表现层）
- Spring控制的bean
	- 业务bean（Service）
	- 功能bean（DataSource等）
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/70fbff9534df44d5bc17964d24440926.png)

**功能不同，如何避免Spring错误加载到SpringMVC中的bean**
加载Spring控制的bean的时候，排除掉SpringMVC控制的bean

- SpringMVC相关bean加载控制
	- SpringMVC加载的bean对应的包均在`com.itheima.controller`包内
- Spring相关bean加载控制
	- 方式一：Spring加载的bean设定扫描范围为`com.itheima`，排除掉controller包内的bean
	- 方式二：Spring加载的bean设定扫描范围为精准扫描，例如service包、dao包等
	- 方式三：不区分两者环境
方式一：
```java
@ComponentScan({"com.itheima.service","com.itheima.dao"})
public class SpringConfig {
}
```
方式二：
```java
@Configuration
//设置spring配置类加载bean时的过滤规则，当前要求排除掉表现层对应的bean
//excludeFilters属性：设置扫描加载bean时，排除的过滤规则
//type属性：设置排除规则，当前使用按照bean定义时的注解类型进行排除
//classes属性：设置排除的具体注解类，当前设置排除@Controller定义的bean
@ComponentScan(value="com.itheima",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ANNOTATION,
        classes = Controller.class
    )
)
public class SpringConfig {
}
```
**@ComponentScan 类注解**
属性：
- excludeFilters：排除扫描路径中加载的bean，需要指定类别（type）与具体项（classes）
- includeFilters：加载指定的bean，需要指定类别（type）与具体项（classes）

**web配置简化开发**
继承`AbstractAnnotationConfigDispatcherServletInitializer`，仅设置配置类类名即可完成开发
```java
public class ServletContainersInitConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{SpringConfig.class};
    }

    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{SpringMvcConfig.class};
    }

    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
```
## Post请求与Get请求
### 解决Post请求中中文乱码问题
在控制器中添加CharacterEncodingFilter()
```
@Override
protected Filter[] getServletFilters(){
	CharacterEncodingFilter filter = new CharacterEncodingFilter();
	filter.setEncoding("utf-8");
	return new Filter[]{filter};
}
```
## 请求参数
### 普通参数
通过url地址传参，地址参数名与形参变量名相同，定义形参即可接收参数
参数名与变量名不同，使用`@RequestParam("name")`
### POJO类型参数
- POJO参数：请求参数名与形参对象属性名相同，定义POJO类型形参即可接收参数

类型
```java
public class User {
	private String name;
	private int age;
	//setter...getter...略
}
```
发送请求和参数
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/41828fb68c3d4705bbd8783d7e647855.png)
后台接收参数
```java
//POJO参数：请求参数与形参对象中的属性对应即可完成参数传递
@RequestMapping("/pojoParam")
@ResponseBody
public String pojoParam(User user){
	System.out.println("pojo参数传递 user ==> "+user);
	return "{'module':'pojo param'}";
}
```
注意：
- POJO参数接收，前端GET和POST发送请求数据的方式不变。
- 请求参数key的名称要和POJO中属性的名称一致，否则无法封装。

### 嵌套POJO类型参数
应用于POJO对象中嵌套了其它的POJO类的情况
- 嵌套POJO参数：请求参数名与形参对象属性名相同，按照对象层次结构关系即可接收嵌套POJO属性参数
```java
public class Address {
	private String province;
	private String city;
	//setter...getter...略
}
public class User {
	private String name;
	private int age;
	private Address address;
	//setter...getter...略
}
```
发送请求
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/9da666c40ed0460cad7e18422c750aec.png)
后台接收参数：
```java
@RequestMapping("/pojoParam")
@ResponseBody
public String pojoParam(User user){
	System.out.println("pojo参数传递 user ==> "+user);
	return "{'module':'pojo param'}";
}
```
注意：
**请求参数key的名称要和POJO中属性的名称一致，否则无法封装**
### 数组类型参数
- 数组参数：请求参数名与形参对象属性名相同且请求参数为多个，定义数组类型即可接收参数
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/bd355a1f9bb24686a955aaaef3851d9c.png)

后台接收参数
```java
//数组参数：同名请求参数可以直接映射到对应名称的形参数组对象中
@RequestMapping("/arrayParam")
@ResponseBody
public String arrayParam(String[] likes){
	System.out.println("数组参数传递 likes ==> "+ Arrays.toString(likes));
	return "{'module':'array param'}";
}
```

发送请求和参数

### 集合类型参数
使用`@RequestParam`注解来解决集合类型参数接收问题，请求参数名与形参对象属性名相同且请求参数为多个。
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/84452bc12e2d4e04bc996dca1dfa084b.png)
后台接收参数：
```java
//集合参数：同名请求参数可以使用@RequestParam注解映射到对应名称的集合对象中作为数据
@RequestMapping("/listParam")
@ResponseBody
public String listParam(@RequestParam List<String> likes){
	System.out.println("集合参数传递 likes ==> "+ likes);
	return "{'module':'list param'}";
}
```
- 集合保存普通参数：请求参数名与形参集合对象名相同且请求参数为多个，`@RequestParam`绑定参数关系
- 对于简单数据类型使用数组会比集合更简单些。

**`@RequestParam`**
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/7159845f23934aa2847802f8817e7ae6.png)
## JSON数据传输参数 :s
现在比较流行的开发方式为异步调用。前后台以异步方式进行交换，传输的数据使用的是JSON。对于JSON数据类型，常见的有三种：
- json普通数组（["value1", "value2", "value3", ...]）
- json对象（[key1:value1, key2:value2, ...]）
- json对象数组（[{key1:value1, ...}, {key2:value2, ...}]）

SpringMVC默认使用的是jackson来处理json的转换，所以需要在pom.xml添加jackson依赖

```xml
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-databind</artifactId>
	<version>2.9.0</version>
</dependency>
```

并且需要在SpringMVC的配置类中开启SpringMVC的注解支持
```java
@Configuration
@ComponentScan("com.itheima.controller")
//开启json数据类型自动转换
@EnableWebMvc
public class SpringMvcConfig {
}
1
```
### JSON普通数组
在参数前添加@RequestBody注解
```
//使用@RequestBody注解将外部传递的json数组数据映射到形参的集合对象中作为数据
@RequestMapping("/listParamForJson")
@ResponseBody
public String listParamForJson(@RequestBody List<String> likes){
	System.out.println("list common(json)参数传递 list ==> "+likes);
	return "{'module':'list common for json param'}";
}
```

### JSON对象数据
传说的数据内容
```json
{
	"name":"itcast",
	"age":15,
	"address":{
		"province":"beijing",
		"city":"beijing"
	}
}
```
后台接收数据
```java
@RequestMapping("/pojoParamForJson")
@ResponseBody
public String pojoParamForJson(@RequestBody User user){
	System.out.println("pojo(json)参数传递 user ==> "+user);
	return "{'module':'pojo for json param'}";
}
```
### JSON对象数组
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/fd69079b762b4629bf2b274d1e17a351.png)
```java
@RequestMapping("/listPojoParamForJson")
@ResponseBody
public String listPojoParamForJson(@RequestBody List<User> list){
	System.out.println("list pojo(json)参数传递 list ==> "+list);
	return "{'module':'list pojo for json param'}";
}
```


**`@EnableWebMvc`**
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/34876e5f2af548a29eece733f0dd71bb.png)
**`@RequestBody`**
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/dcaa77072d3149ec9797163080b7b1c6.png)
## `@RequestBody`与`@RequestParam`区别
- 区别
	- `@RequestParam`用于接收url地址传参，表单传参
	- `@RequestBody`用于接收json数据
- 应用
	- 后期开发中，发送json格式数据为主，@RequestBody应用较广
	- 如果发送非json格式数据，选用@RequestParam接收请求参数

## 日期参数传递
在Contorl类中，将参数设置为日期类型，并且使用`@DateTimeFormat`来注释日期格式
```java
@RequestMapping("/dataParam")
@ResponseBody
public String dataParam(Date date,
						@DateTimeFormat(pattern="yyyy-MM-dd") Date date1,
						@DateTimeFormat(pattern="yyyy/MM/dd HH:mm:ss") Date date2)
	System.out.println("参数传递 date ==> "+date);
	System.out.println("参数传递 date1(yyyy-MM-dd) ==> "+date1);
	System.out.println("参数传递 date2(yyyy/MM/dd HH:mm:ss) ==> "+date2);
	return "{'module':'data param'}";
}
```

**`@DateTimeFormat`**
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/4bc59278429743c281a6db2f9ffaf9c9.png)
## 响应
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/abcf37af318447ef8328a509815c9648.png)
# SSM整合
具体代码见[https://github.com/cug-lucifer/SSM-Demo](https://github.com/cug-lucifer/SSM-Demo)
## Spring整合Mybatis
### 配置
- SpringConfig
- JDBCConfig、jdbc.properties
- MybatisConfig
### 模型
- Book
### 数据层标准开发
- BookService
- BookServiceImpl
### 测试接口
- BookServiceTest
### 事务处理
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/ab7572a442e5408ba5a5808fceca70c3.png)
## Spring整合SpringMVC
- web配置类
- SpringMVC配置类
- Controller开发

## 表现层数据封装
- 设置统一数据返回结果类
```java
public class Result {
    private Object data;
    private Integer code;
    private String msg;
}
```
**注意**
Result类中字段并不是固定不变，根据需要自行增删
提供若干个构造方法，方便操作

## 异常处理器
集中统一处理项目中出现的异常
### 出现异常现象的常见位置和常见诱因：
- 框架内部抛出的异常：因使用不合规导致
- 数据层抛出的异常：外部服务器故障导致
- 业务层抛出异常：业务逻辑书写错误导致
- 表现层抛出的异常：因数据收集、校验等规则导致
- 工具类抛出的异常：工具类书写不严谨

### 异常处理代码书写
所有异常均抛出到表现层进行处理
异常进行费雷处理
使用AOP处理异常

```
@RestControllerAdvice
public class ProjectExceptionAdvice {
    @ExceptionHandler(Exception.class)
    public Result doException(Exception ex){
        System.out.println("发生异常");
        return new Result(666,"发生异常");
    }
}
```

**@RestControllerAdvice**
- 类注解
- 位于Rest风格开发的控制器增强类定义方法上方
- 为Rest风格开发的控制器类做增强
- 此注解自带@ResponseBody注解与@Component注解，具备对应的功能

**@ExceptionHandler**
- 方法注解
- 专用于异常处理器的控制器方法上方
- 设置指定异常的处理方案，功能等同于控制器方法，出现异常后终止原始控制器方法执行，并转入当前方法执行
- 此类方法可以更具处理的异常不同，制作多个方法风别处理对应的异常


### 项目异常分类
- 业务异常
	-  发送对应消息给用户，提醒规范操作
- 系统异常
	- 发送固定消息传递给用户，安抚用户
	- 发送特定消息给运维人员，提醒维护
	- 记录日志
- 其他异常
	- 发送固定消息传递给用户，安抚用户
	- 发送特定消息给编程人员，提醒维护（纳入预期范围）
	- 记录日志

## 拦截器 Interceptor
- 拦截器是一种动态拦截方法调用的机制
- 作用：
	- 在指定方法调用前后执行预先设定后的代码
	- 阻止原始方法的执行

### 拦截器与过滤器的区别
- 归属不同：Filter属于Servlet技术，Interceptor属于SpringMVC技术
- 拦截内容不同：Filter对所有访问进行增强，Interceptor仅针对SpringMVC的访问进行增强

### 案例
1. 声明拦截器的Bean，并实现HandlerInterceptor接口
```java
@Component
public class ProjectInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       System.out.println("PreHandle...");
       return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("PostHandle...");
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("AfterCompletion...");
    }
}
```
2. 定义配置类，继承WebMvcConfigurationSupport，实现addInterceptor方法（注意扫描加载配置）
```
public class SpringMvcSupport extends WebMvcConfigurationSupport {
    @Autowired
    private ProjectInterceptor projectInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
   		......
    }
}
```
3. 添加拦截器并设定拦截的访问路径，路径可以通过可变参数设置多个
```
@Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(projectInterceptor).addPathPatterns("/books");
    }
```

4. 也可以直接通过SpringMVC实现WebMvcConfigurer简化开发
### 拦截器执行顺序
- preHandle
	- return true
		- controller
		- postHandler
		- afterCompletion
	- return false
		- 结束
### 拦截器参数
**前置处理**
 ```java
    @Override
    public boolean preHandle(HttpServletRequest request, 
                             HttpServletResponse response, 
                             Object handler) throws Exception {
       System.out.println("PreHandle...");
       return true;
    }
```
- 参数
	- request: 请求对象，可以获取相应字段内容
	-  response：响应对象，同上
	- handler：被调用的处理器对象，本质上是一个方法对象，对反射技术中的method对象进行了再封装

**后置处理器**
```java
    @Override
    public void postHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler, 
                           ModelAndView modelAndView) throws Exception {
        System.out.println("PostHandle...");
    }
```
- 参数
	- modelAndView：如果处理器执行完成具有返回结果，可以读取到对应数据与页面信息，并进行修改

**完成后处理**
```java
    @Override
    public void afterCompletion(HttpServletRequest request, 
                                HttpServletResponse response, 
                                Object handler, 
                                Exception ex) throws Exception {
        System.out.println("AfterCompletion...");
    }
```
- 参数
	- ex：如果处理器执行过程中出现异常对象，可以针对异常情况进行单独处理。
### 拦截器执行顺序
- 当配置多个拦截器时，形成拦截器链
- 拦截器链的运行顺序参照拦截器添加顺序为准
- 当拦截器中出现对原始拦截器的拦截，后续拦截器君终止运行
- 当拦截器运行中断，仅运行配置在前面的拦截器的afterCompletion操作
![](https://img-blog.csdnimg.cn/direct/6d70086764d448a3a52683bd1a667ac5.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/0d49d3d5f0dc4ce0a2daac94a06e2ef7.png)
- preHandle：与配置顺序相同，必定运行
- postHandle：与配置顺序相反，可能不运行
- afterCompletion：与配置顺序相反，可能不运行

