package chunjae.authserver.common.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import chunjae.authserver.common.security.dto.UsernamePasswordAuthentication;
import chunjae.authserver.common.security.helper.AuthorizationHeaderHelper;
import chunjae.authserver.common.security.helper.TokenHelper;
import chunjae.authserver.common.security.service.SecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InitialAuthenticationFilter extends OncePerRequestFilter {

    private String loginPath;
    private AuthenticationManager manager;
    private TokenHelper tokenHelper;
    private SecurityService securityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // 1. Authorization에서 user 조회
        String[] credentials = AuthorizationHeaderHelper.decodeBasic(request);
        String username = credentials[0];
        String password = credentials[1];

        Authentication authentication = new UsernamePasswordAuthentication(username, password);
        authentication = this.manager.authenticate(authentication);

        // 2. 토큰 생성
        // 2.1 refresh jwt 생성
        String refreshJwt = this.tokenHelper.createRefreshToken(authentication);
        
        // 2.2 access jwt 생성
        String accessJwt = this.tokenHelper.createAccessToken(authentication);
        
        // 2.3 accessJwt, refreshJwt, username 저장 => 마지막 발급된 AccessToken을 이용한, refresh 토큰 탈취 방지용
        securityService.saveUserToken(accessJwt, refreshJwt, username);
        
        // 3. Authorization에 jwt를 넣어준다.
        response.setHeader("A_T", accessJwt);
        response.setHeader("R_T", refreshJwt);
        // end. 다음 필터 호출
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        // login 필터에만 이 필터를 적용
        return !request.getServletPath().equals(this.loginPath);
    }
}
