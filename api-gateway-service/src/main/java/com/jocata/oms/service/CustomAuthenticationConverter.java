package com.jocata.oms.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class CustomAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        List<String> authHeaders=exchange.getRequest().getHeaders().get("Authorization");
        if(authHeaders==null || authHeaders.size()==0){
            return Mono.empty();
        }

        String authHeader=authHeaders.get(0);
        if(!authHeader.startsWith("Basic")){
            return Mono.empty();
        }

        String base64Credentials = authHeader.substring(6);
        byte[] decodeBytes;
        try {
            decodeBytes = Base64.getDecoder().decode(base64Credentials);
        }catch (IllegalArgumentException e){
            return Mono.empty();
        }

        String decodeCredentials = new String(decodeBytes, StandardCharsets.UTF_8);

        String[] decodedCredentials = decodeCredentials.split(":",2);
        if(decodedCredentials.length!=2){
            return Mono.empty();
        }

        String username = decodedCredentials[0];
        String password = decodedCredentials[1];

        return Mono.just(new UsernamePasswordAuthenticationToken(username, password));
    }
}
