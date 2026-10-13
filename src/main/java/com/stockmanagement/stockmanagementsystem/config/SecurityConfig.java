package com.stockmanagement.stockmanagementsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/access-denied", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/categories", "/categories/**")
                                .hasAnyRole("ADMIN", "INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/categories", "/categories/**")
                                .hasAnyRole("ADMIN", "INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/products/new", "/products/edit/**")
                                .hasAnyRole("ADMIN", "INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/products")
                                .hasAnyRole("ADMIN", "INVENTORY_MANAGER", "STOCK_CLERK",
                                        "PURCHASING_OFFICER", "MANAGEMENT")
                        .requestMatchers(HttpMethod.POST, "/products", "/products/**")
                                .hasAnyRole("ADMIN", "INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/suppliers/new", "/suppliers/edit/**")
                                .hasAnyRole("ADMIN", "PURCHASING_OFFICER")
                        .requestMatchers(HttpMethod.GET, "/suppliers")
                                .hasAnyRole("ADMIN", "INVENTORY_MANAGER", "PURCHASING_OFFICER")
                        .requestMatchers(HttpMethod.POST, "/suppliers", "/suppliers/**")
                                .hasAnyRole("ADMIN", "PURCHASING_OFFICER")
                        .requestMatchers("/stock-in/**")
                                .hasAnyRole("ADMIN", "INVENTORY_MANAGER", "STOCK_CLERK", "PURCHASING_OFFICER")
                        .requestMatchers("/stock-out/**")
                                .hasAnyRole("ADMIN", "INVENTORY_MANAGER", "STOCK_CLERK")
                        .requestMatchers("/reports/**")
                                .hasAnyRole("ADMIN", "INVENTORY_MANAGER", "PURCHASING_OFFICER", "MANAGEMENT")
                        .requestMatchers("/low-stock/**").authenticated()
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .failureUrl("/login?error")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
