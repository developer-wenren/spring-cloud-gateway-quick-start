![0b923b36-9a96-4f96-a90c-8c09e2ba982b](https://tva1.sinaimg.cn/large/007S8ZIlgy1ggooswtclhj31lc0u04qp.jpg)

## 认识 Spring Cloud Gateway

Spring Cloud Gateway 是一款基于 Spring 5，Project Reactor 以及 Spring Boot 2 构建的 API 网关，是 Spring Cloud 微服务生态的主要组件之一。Spring Cloud Gateway 主要负责接口请求的路由分发，并且支持对请求的安全验证，流量监控和流量控制等扩展操作。另外值得一提的点是，Spring Cloud Gateway 默认采用了非阻塞 I/O 模型实现请求路由的分发。对于处理一些I/O 耗时长的请求上，相比其他一样用 Java 编写采用的同步阻塞I/O 模型的网关性能更高，处理的并发数也更高，避免了因为 I/O 阻塞（网络调用，数据库操作等）导致线程空闲下来，仍能继续处理响应其他请求。

## Spring Cloud Gateway 适用场景

作为 API 网关，Spring Cloud Gateway 所提供的功能也很强大，集成了对负载均衡，动态路由，访问控制，限流熔断，埋点监控等功能的支持。如果现有的微服务体系是以 Java 生态甚至 Spring 生态为基础的，那么就十分适合使用 Spring Cloud Gateway 作为 API 应用网关了，让聚合管理多个微服务 API，对外进行统一的输出。

同时秉承 Spring 家族的传统，Spring Cloud Gateway 也旨在提供一个简单，且高效的方式来进行 API 路由和请求关注点的扩展，对于已经熟悉 Spring 或者 Spring Boot 的开发者来说，Spring Cloud Gateway 学习成本并不高，利用底层框架所带的注解驱动和自动化配置等特性，使用和扩展起来难度都不算高。

## 快速上手 Spring Cloud Gateway

利用 Spring Cloud Gateway 能快速搭建一个 API 网关，但在这之前，先介绍一下使用 Spring Cloud Gateway 框架所涉及的一些专用概念，来加深对 Spring Cloud Gateway 的认识，方便后面的使用。

- 路由：是 Spring Cloud Gateway 中基础的组件，通常由一个 id 标识，目标 URI，以及一系列断言（Predicate）和过滤器组成。
- 断言（Predicate）：是 Java 8 函数库的 [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html) 对象，具体类型为 `Predicate<ServerWebExchange>` ，用于匹配 HTTP 请求上数据信息，如请求头信息，请求体信息。如果对于某个请求的断言为 true，那么它所关联的路由就算匹配成功，然后将请求给这个路由处理。
- 过滤器：用于某一个路由的请求或者响应进行修改的组件，在 Spring Cloud Gateway 都要实现 GatewayFilter 接口，并且需要由基于 GatewayFilterFactory 具体实现类构造。

![CD7B74D1-0AB2-4A7E-9A7A-B1E305D8898C](https://tva1.sinaimg.cn/large/007S8ZIlgy1ggooiwzbqhj30i60b341x.jpg)

认识上面三个概念之后，再看上图所示，就能清楚看出 Spring Cloud Gateway 对客户端请求的处理过程了，这帮助我们用好 Spring Cloud Gateway 帮助很大。

- 客户端请求首先被 GatewayHandlerMapping 获取，然后根据断言匹配找到对应的路由
- 找到路由后，走完所关联的一组请求过滤器的处理方法，请求到目标 URI 所对应的服务程序，获得服务响应。
- 网关收到响应后，通过关联的响应过滤器的处理方法后，同样由 GatewayHandlerMapping 返回响应给客户端。

额外需要注意的是 Spring Cloud Gateway 的过滤器是有序执行的，统一以 order 值的大小决定执行顺序，值越小优先级越高，就越先执行。

### 如何实现 API 聚合

认识 Spring Cloud Gateway 整体处理请求过程之后，我们现在就快速构建一个基于 Spring Cloud Gateway 的 API 网关，看看在实际应用中还需要注意的哪些地方，需要注意的是本文所使用的 Spring Cloud Gateway 属于最新的里程碑版本 2.2.3，对应 Spring Boot 版本为 2.3.1， 并且 Spring Cloud 版本为 Hoxton.SR6 。利用 [Spring Initializr](https://start.spring.io/) ，选择对应的版本和依赖后快速新建一个项目 `spring-cloud-gateway-quick-start` ，并且为了实现请求的路由，表现网关的效果，再分别新建用户服务应用 `demo-userservice` 和订单服务应用 `demo-orderservice` ，各自提供一个可调用 API 接口。

用户服务暴露 8071 端口，提供 /user/get 接口：

```java
// demo-userservice  项目
@RestController
@RequestMapping("/user")
public class UserServiceController {
    @RequestMapping("/get")
    public User get() {
        return User.mock();
    }
}
```

类似，订单服务暴露 8061 端口，提供 /order/get 接口：

```java
// demo-orderservice 项目
@RestController
@RequestMapping("/order")
public class OrderServiceController {
    @RequestMapping("/get")
    public Order get() {
        return Order.mock();
    }
}
```

接下来要通过 Spring Cloud Gateway 将两个服务接口聚合在 spring-cloud-gateway-quick-start 项目中，首先来看下利用 Spring Cloud Gateway API 方式的实现：

```java
@SpringBootApplication
public class DemogatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemogatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes().route("user-service", r -> r.path("/user/*").uri("http://localhost:8071"))
                .route("order-service", r -> r.path("/order/*").uri("http://localhost:8061"))
                .build();
    }
}
```

接下来要通过 Spring Cloud Gateway 将两个服务接口聚合在 `spring-cloud-gateway-quick-start` 项目中，首先来看下利用 Spring Cloud Gateway API 方式的实现：

上述代码就已经实现 API 路由的功能，是不是很快速，同时启动 `spring-cloud-gateway-quick-start` 和其他服务应用，就可以统一通过网关应用访问用户服务和订单服务了：

```java
one@192 ~ % curl http://localhost:8080/user/get
{"id":4720186416534735290,"token":"86b6118d-7dc6-4d30-a5f3-3d5fc6348f9a"}
  
one@192 ~ % curl http://localhost:8080/order/get
{"id":5832646761962425508,"title":"My Order"}
```

回到 API 实现的代码， `DemogatewayApplication#customRouteLocator` 方法中定义了两个 id 分别为 user-service 和 order-service 的路由，并且设置了匹配请求的断言，以及真正目标请求地址。这里路由的断言采用了路径匹配的规则，只要原始请求地址符合对应的规则就算匹配到此路由，但 Spring Cloud Gate 还支持丰富的断言规则，如主机匹配，请求体字段匹配，请求数据匹配等等，足以满足定制路由断言的规则了。

由于使用 API 就是硬编码方式将路由规则定义在程序里了，这样做扩展性很差，也不好维护。于是更推荐另外一种实现方式：配置化。来看下要实现相同功能，在 application.properties 里该如何配置：

```properties
spring.cloud.gateway.routes[0].id=order-service
spring.cloud.gateway.routes[0].uri=http://localhost:8061
spring.cloud.gateway.routes[0].predicates[0].name=Path
spring.cloud.gateway.routes[0].predicates[0].args[pattern]=/order/*
spring.cloud.gateway.routes[1].id=user-service
spring.cloud.gateway.routes[1].uri=http://localhost:8071
spring.cloud.gateway.routes[1].predicates[0].name=Path
spring.cloud.gateway.routes[1].predicates[0].args[pattern]=/user/*
```

使用上面的配置，重启网关应用，同样能完成之前 API 方式的效果，由于路由规则转移到了配置文件中，就大大方便对 API 的管理，为实现动态路由也提供了可能。当然需要实现动态路由，除了路由配置，还需要进行额外的扩展实现路由规则的动态刷新，涉及 Spring Cloud Gateway 更高级的用法，本文就不再详细赘述了，可以等后续进阶使用和分析的文章或者参考网上其他实现资料。

### 如何自定义过滤器

为了能对 API 的请求或者响应处理，Spring Cloud Gateway 提供过滤器组件来实现这一功能，并且内置了很多功能强大。另外过滤器分两类，全局过滤器和网关过滤器，对于全局过滤器，所有匹配到路由的请求处理时都会经过全局过滤器处理；而网关过滤器只有显示在指定路由上时才会起到左右。

Spring Cloud Gateway 默认的全局过滤器有 8个：

- ForwardRoutingFilter
- LoadBalancerClientFilter（弃用）
- ReactiveLoadBalancerClientFilter
- WebClientHttpRoutingFilter
- NettyWriteResponseFilter
- RouteToRequestUrlFilter
- WebsocketRoutingFilter
- GatewayMetricsFilter

而网关过滤器就更多了，并且由对应工厂类来构造，比如用于熔断的 **HystrixGatewayFilterFactory** ，用于限流的 **RequestRateLimiterGatewayFilterFactory**，用于修改请求数据的 **ModifyRequestBodyGatewayFilterFactory** 等等，当然也支持开发者进行定义自己的过滤器。

首先来看下如何自定义一个全局过滤器，代码实现比较简单：

```java
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    private Logger log = LoggerFactory.getLogger(MyAuthFilterFactory.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("执行自定过滤器");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```

这样就可以为所有路由添加一个全局的过滤器了。不同于全局过滤器的定义，网关过滤器必须在指定路由上进行申明才能生效，参考官方内置的网关拦截器，自定义一个用于授权的简易网关拦截器工厂如下：

```java
@Component
public class MyAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<MyAuthGatewayFilterFactory.Config> {
    private Logger logger = LoggerFactory.getLogger(MyAuthGatewayFilterFactory.class);

    public MyAuthGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                ServerHttpRequest request = exchange.getRequest();
                MultiValueMap<String, String> queryParams = request.getQueryParams();
                String from = queryParams.getFirst(config.getAuthKey());
                ServerHttpResponse response = exchange.getResponse();
                logger.warn("校验授权开始");
                if (config.getAuthValue().equals(from)) {
                    logger.warn("校验授权成功");
                    return chain.filter(exchange);
                } else {
                    logger.warn("校验授权失败");
                    response.setStatusCode(HttpStatus.OK);
                    response.getHeaders().setContentType(MediaType.valueOf("text/html;charset=utf-8"));
                    DataBuffer wrap = response.bufferFactory().wrap(config.getAuthFailMsg().getBytes(Charset.forName("UTF-8")));
                    return response.writeWith(Flux.just(wrap));
                }
            }
        };
    }

    public static class Config {
        private String authKey = "from";
        private String authValue = "system";
        private String authFailMsg = "授权失败";

        public String getAuthKey() {
            return authKey;
        }

        public void setAuthKey(String authKey) {
            this.authKey = authKey;
        }

        public String getAuthValue() {
            return authValue;
        }

        public void setAuthValue(String authValue) {
            this.authValue = authValue;
        }

        public String getAuthFailMsg() {
            return authFailMsg;
        }

        public void setAuthFailMsg(String authFailMsg) {
            this.authFailMsg = authFailMsg;
        }
    }
}
```

如果要在 user-service 路由下使用，需要在 application.properties 配置文件添加如下配置：

```properties
spring.cloud.gateway.routes[1].filters[0].name=MyAuth
```

这里的名称就需要跟 MyAuthGatewayFilterFactory 类的 MyAuth 保持一致，Spring Cloud Gateway 会自动拼接上 **AuthGatewayFilterFactory** 去查找对应的网关过滤器，没有找到就会导致启动失败，抛出异常：

```java
java.lang.IllegalArgumentException: Unable to find GatewayFilterFactory with name MyAuth2
```

配置完对网关应用进行重启，这是使用原来的方式去请求用户服务，已经无法正常访问，只会返回**校验授权失败**的信息，必须以 `http://localhost:8080/user/get?from=system` 方式访问才能成功获取到数据，说明定义的授权拦截器已经起了作用。

这里我们就将全局拦截器和网关拦截器都实现了自定义，通常情况我们都会在网关拦截器上进行扩展定制，也结合内置的过滤器使用。最后将完整的实现代码上传到 Gitlab ：https://github.com/wrcj12138aaa/spring-cloud-gateway-quick-start ，感兴趣的朋友也可以参考下。
