package com.jocata.oms.service;

import com.jocata.oms.form.GenericResponse;
import com.jocata.oms.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final WebClient webClient;

    @Autowired
    public  CustomReactiveUserDetailsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return webClient.get()
                .uri(uriBuilder ->  uriBuilder.scheme("http")
                            .host("localhost")
                            .port(8081)
                            .path("/auth/user")
                            .queryParam("username", username)
                            .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GenericResponse<UserForm>>() {})
                .map(this::convertToUserDetails)
                .doOnSuccess(user->
                        System.out.println("User Details loaded successfully with username: "+ user.getUsername()))
                .doOnError(error->
                        System.out.println("Error while loading user details from database: "+error.getMessage()));
    }

    private UserDetails convertToUserDetails(GenericResponse<UserForm> response) {
        UserForm dto = response.getData();

        List<GrantedAuthority> authorities = dto.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+ role.getRoleName()))
                .collect(Collectors.toList());

        UserDetails build = User.withUsername(dto.getEmail())
                .password(dto.getPasswordHash())
                .authorities(authorities)
                .build();
        System.out.println(build);
        return build;
    }
}
