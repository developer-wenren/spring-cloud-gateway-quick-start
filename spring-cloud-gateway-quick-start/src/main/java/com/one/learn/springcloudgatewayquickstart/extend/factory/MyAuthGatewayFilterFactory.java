package com.one.learn.springcloudgatewayquickstart.extend.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @author one
 * @date 2020/07/12
 */
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
