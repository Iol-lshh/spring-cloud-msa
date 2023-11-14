package chunjae.authserver.common.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.filter.OncePerRequestFilter;

import chunjae.authserver.common.security.helper.TokenHelper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccessAuthenticationFilter extends OncePerRequestFilter{

    private List<String> noAccessRequestPaths;
    private TokenHelper tokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Access 처리
        // 1-1. AccessToken 서명 확인
        Optional<String> maybeJwt = Optional.ofNullable(request.getHeader("A_T"));
        
        if(maybeJwt.isEmpty() || maybeJwt.get().equals("")){
            throw new BadCredentialsException("access 미발견");
        }
        String jwt = maybeJwt.get();

        // 토큰 구문 분석 및 서명 검증
        try {
            tokenHelper.validateAccessToken(jwt);
        } catch (JwtException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT token");
            return;
        }

        // 권한 줄 것인가? 혹은 찾아올 것인가?

        // 다음 필터 호출
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        // noAccessRequestPaths: [login, refresh] 에 이 필터를 적용하지 않는다.
        String requestPath = request.getServletPath();
        return this.noAccessRequestPaths.stream()
                                        .filter(noAccessRequestPath -> noAccessRequestPath.matches(requestPath))
                                        .count() > 0;    
    }
}
