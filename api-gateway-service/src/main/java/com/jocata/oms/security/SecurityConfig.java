package com.jocata.oms.security;

import com.jocata.oms.service.CustomAuthenticationConverter;
import com.jocata.oms.service.CustomReactiveAuthenticationManager;
import com.jocata.oms.service.CustomSecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final CustomReactiveAuthenticationManager authenticationManager;
    private final CustomSecurityContextRepository contextRepository;
    private final CustomAuthenticationConverter authenticationConverter;


    public SecurityConfig(@Lazy CustomReactiveAuthenticationManager authenticationManager,
                          CustomSecurityContextRepository contextRepository,
                          CustomAuthenticationConverter authenticationConverter) {
        this.authenticationConverter = authenticationConverter;
        this.authenticationManager = authenticationManager;
        this.contextRepository = contextRepository;
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter authFilter= new AuthenticationWebFilter(authenticationManager);
        authFilter.setServerAuthenticationConverter(authenticationConverter);
        authFilter.setSecurityContextRepository(contextRepository);
        return authFilter;
    }


    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, AuthenticationWebFilter authenticationWebFilter) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/users/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/v1/users/user/**").hasAnyRole("USER", "ADMIN" )
                        .pathMatchers("/auth/user").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/users/public/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/public/**").permitAll()
                        .anyExchange().authenticated()

                )
                .addFilterAfter(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
