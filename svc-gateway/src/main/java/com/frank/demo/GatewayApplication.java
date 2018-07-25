package com.frank.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class GatewayApplication {

    private static final Log log = LogFactory.getLog(GatewayApplication.class);

    @Autowired
    private DiscoveryClient discoveryClient;

//    @Bean
//    public DiscoveryClientRouteDefinitionLocator discoveryClientRouteLocator(DiscoveryClient discoveryClient,
//                                                                             DiscoveryLocatorProperties properties) {
//        return new DiscoveryClientRouteDefinitionLocator(discoveryClient, properties);
//    }
//
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
                .route("agt", r -> r.path("/agent/**")
                        .filters(f->f.filter((exchange, chain) -> {
                            int id =  Integer.valueOf(exchange.getRequest().getQueryParams().getFirst("id"));
                            ServerHttpRequest request  = exchange.getRequest().mutate()
                                    .path("/"+id).build();

                                    return chain.filter(exchange.mutate().request(request).build());
                        }
                        )).uri("lb://svc-agency-service"))
                .route("greeting", r -> r.path("/gre/**")
                        .uri("lb://svc-risk-service"))
//				.route("test", r -> r.path("/**")
//						.uri("lb://svc-agency-service"))
				.build();
	}


    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class,
                              args);
    }

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
