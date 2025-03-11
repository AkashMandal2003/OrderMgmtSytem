package com.jocata.oms.security;

import com.jocata.oms.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean

    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/users/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/v1/users/user/**").hasAnyRole("USER", "ADMIN")

                        .pathMatchers("/products/**").hasRole("ADMIN")
                        .pathMatchers("/inventory/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/v1/users/public/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/public/**").permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler())
                )
                .addFilterBefore(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    public ServerAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (exchange, ex) -> {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String body = """
                        {
                            "status": 401,
                            "error": "Unauthorized",
                            "message": "Access Denied! Please provide valid credentials."
                        }
                    """;
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(body.getBytes()))
            );
        };
    }

    @Bean
    public ServerAccessDeniedHandler customAccessDeniedHandler() {
        return (exchange, ex) -> {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String body = """
                        {
                            "status": 401,
                            "error": "Unauthorized",
                            "message": "You do not have permission to access this resource."
                        }
                    """;
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(body.getBytes()))
            );
        };

    }


    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> Mono.empty();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return authentication -> {
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                UserDetails user = (UserDetails) authentication.getPrincipal();
                return Mono.just(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            }
            return Mono.empty();
        };
    }

}