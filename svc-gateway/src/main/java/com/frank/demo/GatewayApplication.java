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
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.client.RestTemplate;

import reactor.core.publisher.Mono;

/**
 * docker rmi $(docker images --filter "dangling=true" -q)

 * @author frank
 *
 */
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
                		.filters(f -> f.rewritePath("/gre/(?<segment>.*)",
								"/${segment}"))
//                		.filters(f -> f.setPath("/"))
//                		.filters(f -> f.rewritePath("/gre/(?<segment>.*)",
//								"/${segment}"))
                        .uri("lb://svc-risk-service"))
//				.route("test", r -> r.path("/**")
//						.uri("lb://svc-agency-service"))
				.build();
	}

	@Bean
	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
		return http.httpBasic().and()
				.csrf().disable()
				.authorizeExchange()
				.pathMatchers("/agent/**").authenticated()
				
//				
//				.pathMatchers("/users/{userName}").hasRole("ADMIN")
//				.access(new ReactiveAuthorizationManager<AuthorizationContext>() {
//					@Override
//					public Mono<AuthorizationDecision> check(Mono<Authentication> mono,
//							AuthorizationContext context) {
//						String uname = (String) context.getVariables().get("userName");
////						return null;
//						return mono.map(auth -> auth.getName().equals(uname))
//								.map(AuthorizationDecision::new);
//
//					}
//				})
				
				.anyExchange().permitAll()
				.and()
				.build();
	}

	@Bean
	public MapReactiveUserDetailsService reactiveUserDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder().username("frank").password("frank").roles("USER").build();
		return new MapReactiveUserDetailsService(user);
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
