package chunjae.api.common.security.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.filter.OncePerRequestFilter;

import chunjae.api.common.security.helper.TokenHelper;
import chunjae.api.common.security.service.SecurityService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RefreshLogoutFilter extends OncePerRequestFilter {

    private String logoutPath;
    private TokenHelper tokenHelper;
    private SecurityService securityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // 토큰 조회
        Optional<String> maybeRefreshJwt = Optional.ofNullable(request.getHeader("R_T"));
        Optional<String> maybeUsername = Optional.ofNullable(request.getHeader("username"));
                
        if(maybeRefreshJwt.isEmpty() || maybeRefreshJwt.get().equals("")){
            throw new BadCredentialsException("jwt 미발견");
        }
        // 1. 토큰 구문 분석 및 서명 검증
        String refreshJwt = maybeRefreshJwt.get();
        Claims claims = tokenHelper.validateRefreshToken(refreshJwt);
        
        // 2-1. refresh 토큰 만료
        if(maybeUsername.isEmpty() || maybeUsername.get().equals("")){
            securityService.deleteUserToken(refreshJwt);
        }
        // 2-2. username의 전체 refresh token 만료 (이 서비스에 대해서만, 전체 로그아웃)
        else{
            Optional<String> maybeJwtUsername = Optional.ofNullable(String.valueOf(claims.get("username")));
            if(maybeJwtUsername.isEmpty()){
                securityService.deleteUserToken(refreshJwt);
                throw new BadCredentialsException("Refresh 탈취 의심: 잘못된 형식");
            }
            securityService.deleteUserTokens(maybeJwtUsername.get());
        }
        
        // 다음 필터 호출
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        // login 필터에만 이 필터를 적용
        return !request.getServletPath().equals(this.logoutPath);
    }
}
