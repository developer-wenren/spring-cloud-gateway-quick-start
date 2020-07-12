package com.one.learn.springcloudgatewayquickstart.extend;

import com.one.learn.springcloudgatewayquickstart.extend.factory.MyAuthGatewayFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author one
 */
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    private Logger log = LoggerFactory.getLogger(MyAuthGatewayFilterFactory.class);

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