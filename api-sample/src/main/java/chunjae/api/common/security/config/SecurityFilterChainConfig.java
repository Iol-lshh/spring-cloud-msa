package chunjae.api.common.security.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import chunjae.api.common.security.filter.AccessAuthenticationFilter;
import chunjae.api.common.security.filter.InitialAuthenticationFilter;
import chunjae.api.common.security.filter.RefreshLogoutFilter;
import chunjae.api.common.security.filter.RefreshAuthenticationFilter;
import chunjae.api.common.security.helper.TokenHelper;
import chunjae.api.common.security.service.SecurityService;
import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Configuration
public class SecurityFilterChainConfig{

    @Value("${security.filter-config.initial-authentication-filter.path}")
    private String loginPath;
    @Value("${security.filter-config.refresh-logout-filter.path}")
    private String logoutPath;
    @Value("${security.filter-config.refresh-request.path}")
    private String refreshRequestPath;
    
    private List<String> noAccessRequestPaths;

    @Value("${jwt.access.token-validity-in-days}")
    private int accessValidity;

    @Value("${jwt.refresh.token-validity-in-days}")
    private int refreshValidity;
    
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SecurityService securityService;

    @Autowired
    CorsConfigurationSource corsConfigurationSource;

    @PostConstruct
    public void noAccessRequestPaths(){
        this.noAccessRequestPaths = new ArrayList<>();
        this.noAccessRequestPaths.add(loginPath);
        this.noAccessRequestPaths.add(logoutPath);
        this.noAccessRequestPaths.add(refreshRequestPath);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        
        return http.securityMatcher("/**")
                    // .authorizeHttpRequests(ahrs -> ahrs
                    //     // .anyRequest().hasAnyRole("USER", "ADMIN"))
                    //     .requestMatchers("/**").hasAnyRole("USER", "ADMIN"))
                    // 권한 확인
                    .authorizeHttpRequests(ahrs->ahrs
                        .anyRequest().permitAll())
                    // 프런트에서 처리
                    .httpBasic( hb -> hb.disable())
                    .formLogin( fl -> fl.disable())
                    // cors, csrf 비활성화: jwt로 인증
                    .cors( cors -> cors.configurationSource(corsConfigurationSource))
                    .csrf( csrf -> csrf.disable())
                    // 세션 비활성화: jwt로 처리
                    .sessionManagement( sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    // 최초 로그인 필터
                    .addFilterAt(new InitialAuthenticationFilter(this.loginPath, this.manager, this.tokenHelper, this.securityService), BasicAuthenticationFilter.class)
                    // // 단순 JWT 인증 필터
                    // .addFilterAfter(new JwtAuthenticationFilter(this.loginPath, this.tokenHelper), BasicAuthenticationFilter.class)
                    // 로그아웃 처리
                    .addFilterAfter(new RefreshLogoutFilter(this.logoutPath, this.tokenHelper, this.securityService), BasicAuthenticationFilter.class)
                    // Access 재발급 요청 처리 필터
                    .addFilterAfter(new RefreshAuthenticationFilter(this.refreshRequestPath, this.tokenHelper, this.securityService), BasicAuthenticationFilter.class)
                    // 기타 Request시, 토큰 검증 필터
                    .addFilterAfter(new AccessAuthenticationFilter(this.noAccessRequestPaths, this.tokenHelper), BasicAuthenticationFilter.class)
                    // 예외
                    .exceptionHandling(eh->eh
                        // 인증 단계 에러
                        .authenticationEntryPoint((request, response, authException)->{
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            objectMapper.writeValue(response.getOutputStream(), authException);
                        })
                        // 진입 거부
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            objectMapper.writeValue(response.getOutputStream(), accessDeniedException);
                        }))
                    .build();
    }
}
