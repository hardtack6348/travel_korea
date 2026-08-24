package kr.co.mycom.travel_korea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //jwt토큰을 사용하므로 csrf비활성화 -> localstorage에 저장시 비활성화 아니면 활성화
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//security에서 사용하는 session 비활성화
//                .and()
                .authorizeHttpRequests(auth ->auth.anyRequest().permitAll());
//                .oauth2Login(Customizer.withDefaults());
//                .antMatchers("/api/v1/img/").permitAll()
//                .antMatchers("/api/v1/auth/n/").permitAll() // 사용자  토큰x
//                .antMatchers("/api/v1/auth/y/**").hasAnyAuthority("ROLE_USER", "ROLE_TT_ADMIN", "ROLE_ST_ADMIN") // 사용자  토큰o
//                .antMatchers("/api/v1/main/").permitAll() // 모든 사용자
//                .antMatchers("/api/v1/admin/").hasAnyAuthority("ROLE_TT_ADMIN") // 최고 관리자
//                .antMatchers("/api/v1/store/admin/").hasAnyAuthority("ROLE_ST_ADMIN", "ROLE_TT_ADMIN") // 가맹점 관리자
//                .antMatchers("/api/v1/store/").permitAll()
//                .authenticated()  // 나머지 요청은 인증이 필요함

//                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())  // 사용자 인증 실패 처리
//                .accessDeniedHandler(new CustomAccessDeniedHandler())  // 권한 없음 처리
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) // 사용자 인증 필터 추가
//                .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class);  // JWT 예외 처리 필터 추가
        return http.build();
    }
}
