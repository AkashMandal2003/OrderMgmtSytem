package com.jocata.oms.service;

import com.jocata.oms.forms.GenericResponse;
import com.jocata.oms.forms.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@Primary
@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final String BASE_URL = "http://localhost:8081/api/v1/users/user";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        UserForm userByEmail = getUserByEmail(username);
        UserDetails userDetails = convertToUserDetails(userByEmail);
        return Mono.just(userDetails);
    }

    private UserForm getUserByEmail(String email) {
        URI uri = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("email", email)
                .build()
                .toUri();

        ResponseEntity<GenericResponse<UserForm>> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<GenericResponse<UserForm>>() {
                }
        );

        GenericResponse<UserForm> response = responseEntity.getBody();

        if (response == null || response.getData() == null) {
            throw new UsernameNotFoundException("User data not found for email: " + email);
        }
        return response.getData();
    }

    private UserDetails convertToUserDetails(UserForm userForm) {

        Set<GrantedAuthority> authorities = userForm.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toSet());

        return User.withUsername(userForm.getEmail())
                .password(userForm.getPasswordHash())
                .authorities(authorities)
                .build();
    }

}
