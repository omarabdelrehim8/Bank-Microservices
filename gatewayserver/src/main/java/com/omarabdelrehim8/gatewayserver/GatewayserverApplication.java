package com.omarabdelrehim8.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	// define custom routes path
	@Bean
	public RouteLocator starbankRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(p -> p
					.path("/covetousbank/accounts/**")
					.filters(f -> f.rewritePath("/covetousbank/accounts/(?<segment>.*)", "/${segment}")
						.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
					.uri("lb://ACCOUNTS"))
				.route(p -> p
					.path("/covetousbank/cards/**")
					.filters(f -> f.rewritePath("/covetousbank/cards/(?<segment>.*)", "/${segment}")
						.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
					.uri("lb://CARDS")).build();
	}

}
