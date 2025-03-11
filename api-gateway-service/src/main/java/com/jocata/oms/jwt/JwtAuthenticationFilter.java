package com.jocata.oms.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final String SECRET_KEY = "a6eac1a0ae42d3e466b45dd4610e18decdb339b59bda075853ae0229fd4edc35";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getURI().getPath().startsWith("/api/v1/users/public")) {
            return chain.filter(exchange);
        }

        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeaders.get(0).substring(7);

        try {
            Claims claims = validateToken(token);

            String email = claims.get("sub", String.class);
            List<String> roles = claims.get("roles", List.class);
            List<String> permissions = claims.get("permissions", List.class);

            if (email == null || roles == null) {
                return unauthorizedResponse(exchange, "Invalid JWT claims");
            }

            System.out.println("Authenticated User: " + email);
            System.out.println("Roles: " + roles);
            System.out.println("Permissions: " + (permissions != null ? permissions : "None"));

            // Convert roles to Spring Security authorities format
            List<String> grantedRoles = roles.stream().map(role -> "ROLE_" + role.toUpperCase()).toList();
            User user = new User(email, "", grantedRoles.stream().map(org.springframework.security.core.authority.SimpleGrantedAuthority::new).toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            SecurityContext securityContext = new SecurityContextImpl(authentication);

            // Modify request to include headers
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Roles", String.join(",", grantedRoles))
                    .header("X-User-Permissions", permissions != null ? String.join(",", permissions) : "")
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        } catch (Exception e) {
            System.err.println("JWT validation failed: " + e.getMessage());
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }
    }

    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        System.err.println("Unauthorized: " + message);
        return response.setComplete();
    }
}