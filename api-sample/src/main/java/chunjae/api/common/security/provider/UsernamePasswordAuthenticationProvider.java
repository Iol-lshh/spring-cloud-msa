package chunjae.api.common.security.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import chunjae.api.common.security.dto.UsernamePasswordAuthentication;
import chunjae.api.common.security.service.SecurityService;

@Component
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SecurityService securityService;    // 인증 받아올 위치 - MSA 또는 OTP 이용시 프록시로 구현한다.

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        // 회원 확인 (없다면, UsernameNotFoundException)
        UserDetails userDetails = securityService.loadUserByUsername(username);
        
        // 비밀번호 체크
        if(userDetails.getPassword().equals(password) 
            && userDetails.isCredentialsNonExpired()){
            return new UsernamePasswordAuthentication(username, password);
            // GrantedAuthority 권한들을 넣어줄 것
            // return new UsernamePasswordAuthenticationToken(username, password, authorityList);
        }else{
            throw new BadCredentialsException(username + ": 잘못된 비밀번호");
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        // Authentication의 UsernamePasswordAuthentication을 지원
        return UsernamePasswordAuthentication.class.isAssignableFrom(aClass);
    }
}
