package com.deloittevg.config;

import com.deloittevg.service.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthConverter() {

        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,JpaUserDetailsService jpaUserDetailsService) throws Exception {
        //oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthConverter())
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.POST,"/user/register").permitAll();
                    auth.requestMatchers(HttpMethod.GET,"/user/all").hasRole("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .oauth2ResourceServer((oauth2-> oauth2.jwt(jwt -> jwtAuthConverter())))
                .oauth2Login(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .userDetailsService(jpaUserDetailsService)
                .logout(log -> log.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")).logoutSuccessUrl("/user/login"))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

}
