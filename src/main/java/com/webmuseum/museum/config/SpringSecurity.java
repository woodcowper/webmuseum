package com.webmuseum.museum.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.webmuseum.museum.service.impl.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/resources/**", "/static/**", "/js/**", "/css/**","/webjars/**").permitAll()
                .requestMatchers("/ajax/**").permitAll()
                .requestMatchers("/main/**").permitAll()
                .requestMatchers("/image/**").permitAll()
                //.requestMatchers("/manager/**").permitAll()
                .requestMatchers("/auth/register/**").permitAll()
                .requestMatchers("/auth/index").permitAll()
                .requestMatchers("/client/**").hasRole("CLIENT")
                .requestMatchers("/client/**", "/manager/**").hasRole("MANAGER")
                .requestMatchers("/client/**", "/manager/**", "/admin/**").hasRole("ADMIN")
                .and()
                .userDetailsService(userDetailsService)
                .formLogin(
                        form -> form
                                .loginPage("/auth/login")
                                .loginProcessingUrl("/auth/login")
                                .defaultSuccessUrl("/main/main", true)
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                                .permitAll()

                );
        return http.build();
    }
}