package chunjae.api.common.security.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.filter.OncePerRequestFilter;

import chunjae.api.common.security.dto.UserTokenDto;
import chunjae.api.common.security.helper.TokenHelper;
import chunjae.api.common.security.service.SecurityService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RefreshAuthenticationFilter extends OncePerRequestFilter{

    private String refreshRequestPath;
    private TokenHelper tokenHelper;
    private SecurityService securityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Refresh 처리 (param: Refresh, Access)
        // 1. Refresh 서명 확인
        //    - 고비용을 초기에 거를 수 있음
        Optional<String> maybeRefreshJwt = Optional.ofNullable(request.getHeader("R_T"));
        if(maybeRefreshJwt.isEmpty()|| maybeRefreshJwt.get().equals("")){
            throw new BadCredentialsException("R_T 미발견");
        }
        String refreshJwt = maybeRefreshJwt.get();

        Claims refreshClaims = tokenHelper.validateRefreshToken(refreshJwt);
        String username = String.valueOf(refreshClaims.get("username"));

        // 2. DB에서 token row 가져오기
        Optional<UserTokenDto> maybeUserTokenDto = securityService.loadUserTokenDtoByRefreshToken(refreshJwt);
        if(maybeUserTokenDto.isEmpty()){
            throw new BadCredentialsException("유효하지 않은 R_T");
        }
        UserTokenDto loadedTokenDto = maybeUserTokenDto.get();

        // 3. 탈취 확인: AccessToken과 RefreshToken 동일 여부 확인 => 둘 중 하나라도 다르다면, RefreshToken 폐기
        //      - AccessToken이 기존과 다르다는 것은, RefreshToken이 탈취되었다는 것
        Optional<String> maybeAccessJwt = Optional.ofNullable(request.getHeader("A_T"));
        // 3-1. access 미발견 (탈취된 가능성)
        if(maybeAccessJwt.isEmpty()|| maybeAccessJwt.get().equals("")){
            securityService.deleteUserToken(refreshJwt);
            throw new BadCredentialsException("refresh 탈취 우려1: A_T 미발견");
        }
        // 3-2. 마지막 발급된 access와 불일치 (탈취 가능성)
        else if(!loadedTokenDto.getAccess().equals(maybeAccessJwt.get())){
            securityService.deleteUserToken(refreshJwt);
            throw new BadCredentialsException("refresh 탈취 우려2: 불량 A_T");
        }
        String accessJwt = maybeAccessJwt.get();   
        
        // 4. RefreshToken 만료일이 7일 이하라면, 신규 RefreshToken 발급 (history를 남길 것인가?)        
        if((refreshClaims.getExpiration().getTime())/(24*60*60) - (new Date().getTime())/(24*60*60) < 7){
            // 기존 db 토큰 비활성화
            securityService.deleteUserToken(refreshJwt);
            // 신규 토큰 생성 및 갱신
            refreshJwt = tokenHelper.createRefreshToken(username);
        }

        // 5. 신규 AccessToken 발급
        accessJwt = tokenHelper.createAccessToken(username);

        // 6. DB에 넣기
        securityService.saveUserToken(accessJwt, refreshJwt, username);

        // 7. Header에 jwt를 넣어준다.
        response.setHeader("A_T", accessJwt);
        response.setHeader("R_T", refreshJwt);

        // 다음 필터 호출
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        // refresh 요청에 이 필터를 적용하지 않는다.
        return !request.getServletPath().equals(this.refreshRequestPath);
    }
}
