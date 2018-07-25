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
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class GatewayApplication {

    private static final Log log = LogFactory.getLog(GatewayApplication.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    public DiscoveryClientRouteDefinitionLocator discoveryClientRouteLocator(DiscoveryClient discoveryClient,
                                                                             DiscoveryLocatorProperties properties) {
        return new DiscoveryClientRouteDefinitionLocator(discoveryClient, properties);
    }
    
//	@Bean
//	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//		return builder.routes()
////				.route("fortune_api", p -> p.path("/v2/fortune").and().host("api.monolith.com")
////						.filters(f -> f.setPath("/fortune")
////								.requestRateLimiter().rateLimiter(RedisRateLimiter.class,
////										c -> c.setBurstCapacity(1).setReplenishRate(1))
////								.configure(c -> c.setKeyResolver(exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst("X-Fortune-Key")))))
////						.uri("lb://fortune"))
////				.route("fortune_rewrite", p -> p.path("/service/randomfortune")
////						.filters(f -> f.setPath("/fortune")
////								.hystrix(c -> c.setFallbackUri("forward:/defaultfortune")))
////						.uri("lb://fortune"))
////				.route("hello_rewrite", p -> p.path("/service/hello/**")
////						.filters(f -> f.filter((exchange, chain) -> {
////							String name = exchange.getRequest().getQueryParams().getFirst("name");
////							String path = "/hello/"+name;
////							ServerHttpRequest request = exchange.getRequest().mutate()
////									.path(path)
////									.build();
////							return chain.filter(exchange.mutate().request(request).build());
////						}))
////						.uri("lb://hello"))
////				.route("index", p -> p.path("/")
////						.filters(f -> f.setPath("/index.html"))
////						.uri("lb://ui"))
////				.route("hello-service", p -> p.path("/hello").or().path("/hello/**")
////						.uri("http://hello"))
////				 .route("monolith", p -> p.path("/monolith").or().path("/monolith/**")
////						.uri("http://monolith"))
//				.route("monolith", p -> p.path("/**")
//						.uri("http://monolith"))
//				.build();
//	}


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
