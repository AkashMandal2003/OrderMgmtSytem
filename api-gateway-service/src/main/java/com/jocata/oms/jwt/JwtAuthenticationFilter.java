package com.jocata.oms.jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
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
            return unauthorizedResponse(exchange);
        }

        String token = authHeaders.get(0).substring(7);

        try {
            Claims claims = validateToken(token);

            System.out.println(claims.get("sub"));
            System.out.println(claims.get("roles", List.class));
            System.out.println(claims.get("permissions", List.class));

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Email", claims.get("sub", String.class))
                    .header("X-User-Roles", String.join(",", claims.get("roles", List.class)))
                    .header("X-User-Permissions", String.join(",", claims.get("permissions", List.class)))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            System.out.println("Error");
            return unauthorizedResponse(exchange);
        }
    }

    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

}