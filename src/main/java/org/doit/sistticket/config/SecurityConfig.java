package org.doit.sistticket.config;

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

    // 기본 시큐리티 전체 보안 규칙 설정
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 1. URL별 접근 권한
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/concert/list", "/signup",
                        "/css/**",
                        "/images/**",
                        "/js/**",
                        "/resources/**",
                        "/favicon.ico").permitAll()
                .anyRequest().authenticated())
                // 2. 로그인 페이지 설정, 성공 시 url, 로그인 페이지 권한
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/",false)
                        .permitAll())

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true))
                .csrf(csrf -> csrf.disable())
        // 인증 실패 및 권한 없음 예외
                .exceptionHandling(exception -> exception
                                .authenticationEntryPoint(
                                (request, response, authException) ->{
                                    response.sendRedirect("/login");
                                })
                                .accessDeniedHandler((request, response, authException) ->{
                                    response.sendRedirect("/access-denied");
                                })
                        );
        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
