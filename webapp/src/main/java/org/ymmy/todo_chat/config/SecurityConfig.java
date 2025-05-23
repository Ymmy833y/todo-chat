package org.ymmy.todo_chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .formLogin(formLoginConfigurer -> formLoginConfigurer
            .loginPage("/login")
            .defaultSuccessUrl("/home", true)
            .permitAll())
        .authorizeHttpRequests(requestMatcherRegistry -> requestMatcherRegistry
            .requestMatchers("/css/**", "/js/**").permitAll()
            .anyRequest().authenticated()
        );

    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
