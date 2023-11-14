package chunjae.authserver.common.security.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import chunjae.authserver.common.security.provider.UsernamePasswordAuthenticationProvider;

@Configuration
public class SecurityPreConfig {

    @Value("${security.filter-config.ignore-list}")
    List<String> ignoreList;

    // 인증 공급자
    @Autowired
    private UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

    // 인증 매니저
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        // 인증 공급자 주입
        authenticationManagerBuilder.authenticationProvider(usernamePasswordAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    // 시큐리티 기타 설정
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        // 시큐리티 제외 요청경로
        List<String> _ignoreList = new ArrayList<>();
        _ignoreList.addAll(this.ignoreList);
        _ignoreList.addAll(Arrays.asList("/home", "/home/jsonList", "/swagger-ui/**"));
    
        return web -> web.ignoring().requestMatchers(_ignoreList.toArray(new String[0]));
    }

    // Cors 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.addAllowedOrigin("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
