package com.tms.assignment_jsb.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tms.assignment_jsb.repository.UserGroupsRepository;
import com.tms.assignment_jsb.service.AppUserDetailsService;
import com.tms.assignment_jsb.service.JwtService;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private AppUserDetailsService appUserDetailsService;

  @Autowired
  private UserGroupsRepository userGroupsRepository;

  @Bean
  BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(appUserDetailsService); // Use your custom UserDetailsService
    authProvider.setPasswordEncoder(passwordEncoder()); // Use BCrypt for password encoding
    return authProvider;
  }
  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  JwtAuthenticationFilter jwtAuthenticationFilter() {
      return new JwtAuthenticationFilter(jwtService, appUserDetailsService, userGroupsRepository);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(List.of("http://localhost:5173")); // Allow requests from React frontend
    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // Allow HTTP methods
    corsConfig.setAllowedHeaders(List.of("Authorization", "X-Requested-With", "Content-Type")); // Allow headers
    corsConfig.setAllowCredentials(true); // Allow cookies and credentials

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig); // Apply CORS to all endpoints
    return source;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
  return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
              .requestMatchers("/login").permitAll()
              .requestMatchers("/landing", "/profile", "/updateEmail", "/updatePw").authenticated()
              .requestMatchers("/getUsersInfo", "createGrp", "/createUser", "/update").authenticated()
              .anyRequest().authenticated()
            )
            .logout(logout -> logout
              .logoutSuccessUrl("/logout1") // Parses custom endpoint after spring default logout 
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> httpBasic.disable())
            .build();
  }

}