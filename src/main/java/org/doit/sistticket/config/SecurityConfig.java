package org.doit.sistticket.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 기본 시큐리티 전체 보안 규칙 설정
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 1. URL별 접근 권한
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/", "/concert/list", "/signup",
                                "/api/concert/**","/uploadAjax","/concert/**", "/signup",
                                "/settings/**", //관리자용이라 지울거임!!!!
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/resources/**",
                                "/favicon.ico",
                                "/api/reviews/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                // 2. 로그인 페이지 설정, 성공 시 url, 로그인 페이지 권한
                .formLogin(form -> form
                        .loginProcessingUrl("/api/member/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((req, res, auth) -> {
                            boolean isAdmin = auth.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                            res.setStatus(HttpServletResponse.SC_OK);
                            res.setContentType("application/json;charset=UTF-8");

                            res.getWriter().write(
                                    "{\"redirectUrl\":\""
                                            + (isAdmin
                                            ? "http://localhost:5173/admin"
                                            : "http://localhost:80")
                                            + "\"}"
                            );
                        })
                        .failureHandler((req, res, exception) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");

                            String code ="LOGIN_FAILED";
                            String message = "이메일 또는 비밀번호를 확인해주세요.";
                            // CustomUserDetails에서 예외 걸린 후 처리
                            if(exception instanceof LockedException){
                                code = "SUSPENDED";
                                message = "정지된 계정입니다. 관리자에게 문의 주세요.\\n" +
                                        "Tel. 010-9341-6553";
                            } else if(exception instanceof DisabledException){
                                code ="INACTIVE";
                                message = "탈퇴했거나 비정상적인 행위로 인해 " +
                                        "삭제된 계정입니다.";
                            }
                            res.getWriter().write(
                                    "{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}"
                            );
                        })
                        .permitAll()
                )
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", config);

        return source;
    }
}