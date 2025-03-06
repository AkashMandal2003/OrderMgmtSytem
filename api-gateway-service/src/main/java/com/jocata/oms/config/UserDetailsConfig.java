/*
package com.jocata.oms.config;

import com.jocata.oms.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class UserDetailsConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public UserDetailsConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(List.of(daoAuthenticationProvider));
    }

//    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//
//        UserDetails admin = User.builder()
//                .username("suresh")
//                .password(passwordEncoder().encode("admin123")) // ✅ Secure Password
//                .roles("ADMIN") // ✅ Assigns ROLE_ADMIN
//                .build();
//
//        UserDetails user = User.builder()
//                .username("akash")
//                .password(passwordEncoder().encode("akash")) // ✅ Secure Password
//                .roles("USER") // ✅ Assigns ROLE_USER
//                .build();
//        return new MapReactiveUserDetailsService(user, admin);
//    }
//
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
*/
