package com.jocata.oms.security;

import com.jocata.oms.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }
    

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {

        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/users/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/v1/users/user/**").hasAnyRole("USER", "ADMIN" )
                        .pathMatchers(HttpMethod.GET, "/api/v1/users/public/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/public/**").permitAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(customAuthenticationEntryPoint()));
        return http.build();
    }

    @Bean
    public ServerAuthenticationEntryPoint customAuthenticationEntryPoint() {

        return (exchange, ex) -> {
            ServerWebExchange response = exchange;
            response.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String body = """
                {
                    "status": 401,
                    "error": "Unauthorized",
                    "message": "Access Denied! Please provide valid credentials."
                }
            """;
            return response.getResponse().writeWith(
                    Mono.just(response.getResponse()
                            .bufferFactory()
                            .wrap(body.getBytes()))
            );

        };

    }

    @Bean
    public ReactiveAuthenticationManager customAuthenticationManager(CustomUserDetailsService customUserDetailsService) {
       return authentication ->  customUserDetailsService.findByUsername(authentication.getName())
               .map(userDetails -> new UsernamePasswordAuthenticationToken(
                       userDetails, authentication.getCredentials(), userDetails.getAuthorities()
               ));
    }


    @Bean
    public ServerAuthenticationConverter serverAuthenticationConverter(){
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))
                .filter(authHeader-> authHeader.startsWith("Basic "))
                .map(authHeader -> authHeader.substring(6))
                .map(authToken -> new UsernamePasswordAuthenticationToken(authToken, authToken));
    }

}
