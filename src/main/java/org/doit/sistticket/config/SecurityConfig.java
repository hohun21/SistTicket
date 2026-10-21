package org.doit.sistticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .cors(cors-> {})
                // 1. URL별 접근 권한
            .authorizeHttpRequests(auth -> auth
     .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/", "/concert/list", "/signup",
						"/api/concert/**","/uploadAjax","/concert/**", "/signup",
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
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((req, res, auth) -> {
                            System.out.println("로그인 성공 핸들러 실행");
                            System.out.println("사용자: " + auth.getName());
                            System.out.println("권한: " + auth.getAuthorities());

                            boolean isAdmin = auth.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                            if (isAdmin) {
                                System.out.println("관리자 분기 실행");
                                res.sendRedirect("http://localhost:5173/admin");
                                return;
                            }

                            System.out.println("일반 사용자 분기 실행");
                            res.sendRedirect("/");
                        })
                        .permitAll())

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true))
                .csrf(csrf -> csrf.disable())
        // 인증 실패 및 권한 없음 예외
                .exceptionHandling(exception -> exception
                                .authenticationEntryPoint(
                                (request, response, authException) ->{
                                    response.sendRedirect("http://localhost:5173/member/login");
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

        config.setAllowedOrigins((
                List.of("http://localhost:5173")
                ));
        config.setAllowedHeaders(List.of("*"));

        // 세션 쿠키 사용
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", config);

        return source;
    }


}
