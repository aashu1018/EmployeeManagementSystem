package com.ems.employeemanagementsystem.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${app.security.admin.username}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Value("${app.security.user.username}")
    private String userUsername;

    @Value("${app.security.user.password}")
    private String userPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username(adminUsername)
                .password(passwordEncoder().encode(adminPassword))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username(userUsername)
                .password(passwordEncoder().encode(userPassword))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/actuator/health").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/employees/**").hasAnyRole("ADMIN", "USER")
                    .antMatchers(HttpMethod.POST, "/api/employees").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT, "/api/employees/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/api/departments/**").hasAnyRole("ADMIN", "USER")
                    .antMatchers(HttpMethod.POST, "/api/departments").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/api/leaves").hasAnyRole("ADMIN", "USER")
                    .antMatchers(HttpMethod.PUT, "/api/leaves/*/status").hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/api/leaves/**").hasAnyRole("ADMIN", "USER")
                    .anyRequest().authenticated()
                .and()
                .httpBasic();

        return http.build();
    }
}
