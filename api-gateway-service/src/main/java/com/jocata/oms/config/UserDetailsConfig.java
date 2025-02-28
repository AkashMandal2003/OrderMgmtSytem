package com.jocata.oms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserDetailsConfig {

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("suresh")
                .password(passwordEncoder().encode("admin123")) // ✅ Secure Password
                .roles("ADMIN") // ✅ Assigns ROLE_ADMIN
                .build();

        UserDetails user = User.builder()
                .username("akash")
                .password(passwordEncoder().encode("akash")) // ✅ Secure Password
                .roles("USER") // ✅ Assigns ROLE_USER
                .build();
        return new MapReactiveUserDetailsService(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
