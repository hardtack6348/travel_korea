package kr.co.mycom.travel_korea.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
               .csrf(csrf -> csrf.disable())

                /*
                 * JWT 방식이므로 서버 세션을 생성하지 않습니다.
                 */
               .sessionManagement(session ->
                       session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
               )
               .authorizeHttpRequests(auth -> auth
                                // 로그인, 회원가입, 비밀번호 찾기는 비로그인 접근 허용
                               .requestMatchers("/api/v1/auth/**").permitAll()
                       .requestMatchers("/error").permitAll()
                       // 회원가입 화면에서 쓰는 이메일·닉네임 중복 확인은 비로그인 접근 허용
                       .requestMatchers(HttpMethod.GET, "/api/v1/users/check-email", "/api/v1/users/check-nickname").permitAll()
                                // 공지 목록과 상세 조회는 모든 사용자에게 공개
                       .requestMatchers("/api/v1/notices", "/api/v1/notices/**").permitAll()

                       // 기존 공개 여행 정보 조회
                       .requestMatchers("/api/v1/home", "/api/v1/search", "/api/v1/regions/**", "/api/v1/classifications", "/api/v1/festivals/**").permitAll()

                       // 공개 피드 조회
                       .requestMatchers(HttpMethod.GET, "/api/v1/feed/posts/**").permitAll()
                       .requestMatchers(HttpMethod.GET, "/api/v1/tour/contents/**").permitAll()
                       /*
                        * 공지 등록·수정·삭제는 관리자만 허용합니다.
                        *
                        * DB GRADE가 ADMIN이면 ROLE_ADMIN을 사용합니다.
                        */
                       .requestMatchers("/api/v1/admin/**")
                       .hasAuthority("ROLE_ADMIN")

                       // 나머지 API는 로그인 사용자만 접근
                       .anyRequest().authenticated()
               )
               /*
                * 인증 실패와 권한 부족을 구분해 응답합니다.
                * - 401: 토큰이 없거나, 잘못됐거나, 만료됨 → 프론트엔드는 /refresh로 재발급을 시도합니다.
                * - 403: 로그인은 했지만 권한이 없음(예: 일반 회원이 관리자 API 호출) → 재발급해도 소용없습니다.
                * 설정하지 않으면 Spring Security 기본값으로 두 경우 모두 403이 반환됩니다.
                */
               .exceptionHandling(exception -> exception
                       .authenticationEntryPoint((request, response, authException) ->
                               writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                                       "로그인이 필요하거나 인증이 만료되었습니다."))
                       .accessDeniedHandler((request, response, accessDeniedException) ->
                               writeError(response, HttpServletResponse.SC_FORBIDDEN,
                                       "이 요청에 대한 권한이 없습니다."))
               )
               /*
                * JWT 인증 Filter를 기본 로그인 Filter 이전에 실행합니다.
                */
               .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
    }

    // 다른 API의 오류 응답과 같은 형태({"message": "..."})로 JSON을 씁니다. 메시지는 고정 문구라 이스케이프가 필요 없습니다.
    private static void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
