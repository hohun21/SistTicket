
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

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(
                    "/settings/**",
                    "/",
                    "/concert/list",
                    "/login",
                    "/error",
                    "/access-denied",
                    "/signup",
                    "/api/main/**",
                    "/api/member/**",
                    "/api/concert/**",
                    "/uploadAjax",
                    "/concert/**",
                    "/css/**",
                    "/images/**",
                    "/js/**",
                    "/resources/**",
                    "/favicon.ico",
                    "/api/reviews/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
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
                                : "http://localhost:5173")
                            + "\"}"
                    );
                })
                .failureHandler((req, res, exception) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json;charset=UTF-8");

                    String code = "LOGIN_FAILED";
                    String message = "이메일 또는 비밀번호를 확인해주세요.";

                    if (exception instanceof LockedException) {
                        code = "SUSPENDED";
                        message = "정지된 계정입니다. 관리자에게 문의 주세요.\n"
                            + "Tel. 010-9341-6553";
                    } else if (exception instanceof DisabledException) {
                        code = "INACTIVE";
                        message = "탈퇴했거나 비정상적인 행위로 인해 "
                            + "삭제된 계정입니다.";
                    }

                    res.getWriter().write(
                        "{\"code\":\"" + code
                            + "\",\"message\":\"" + message + "\"}"
                    );
                })
                .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessHandler((request, response, authentication) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"success\":true}");
                    })
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                )
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write(
                            "{\"success\":false,\"message\":\"로그인이 필요합니다.\"}"
                        );
                    } else {
                    	response.sendRedirect("http://localhost:5173/member/login");
                    }
                })
                .accessDeniedHandler((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write(
                            "{\"success\":false,\"message\":\"접근 권한이 없습니다.\"}"
                        );
                    } else {
                        response.sendRedirect("/access-denied");
                    }
                })
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
