package com.examenJava.comptamatiere;

import com.examenJava.comptamatiere.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UtilisateurService utilisateurService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ✅ ADMIN seulement
                        .requestMatchers("/stock/**", "/categories/**", "/admin/**", "/utilisateurs/**").hasRole("ADMIN")

                        // ✅ Authentifié (ADMIN ou USER)
                        .requestMatchers(
                                "/dashboard",
                                "/stock/inventaire/**",
                                "/stock/inventaire/pdf",
                                "/stock/inventaire/excel"
                        ).authenticated()

                        // ✅ Public (accessible à tous)
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()

                        // Tout le reste : authentifié
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                // (facultatif) accès à H2 console
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}
