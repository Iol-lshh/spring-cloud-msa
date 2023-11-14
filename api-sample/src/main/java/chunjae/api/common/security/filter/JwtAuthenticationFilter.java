package chunjae.api.common.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import chunjae.api.common.security.dto.UsernamePasswordAuthentication;
import chunjae.api.common.security.helper.TokenHelper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private String loginPath;
    private TokenHelper tokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        String jwt = Optional.ofNullable(request.getHeader("Authorization")).orElse("");
        
        if(!jwt.equals("")){

            // 토큰 구문 분석 및 서명 검증
            Claims claims = tokenHelper.validateAccessToken(jwt);

            String username = String.valueOf(claims.get("username"));
            String password = String.valueOf(claims.get("password"));
            
            // 권한 제공
            GrantedAuthority authority = new SimpleGrantedAuthority("USER");
            var auth = new UsernamePasswordAuthentication(username, password, List.of(authority));
            
            // SecurityContext에 Authentication 객체를 추가
            SecurityContextHolder.getContext()
                                .setAuthentication(auth);
        }else{
            throw new BadCredentialsException("jwt 미발견");
        }
        
        // 다음 필터 호출
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        // login에 이 필터를 적용하지 않는다.
        return request.getServletPath().matches(this.loginPath);
    }
}
