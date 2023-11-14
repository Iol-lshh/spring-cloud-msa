package chunjae.api.common.security.dto;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UsernamePasswordAuthentication extends UsernamePasswordAuthenticationToken {
    // 미인증 상태
    public UsernamePasswordAuthentication(Object principal, Object credentials){
        super(principal, credentials);
    }

    // 인증 상태
    public UsernamePasswordAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities){
        super(principal, credentials, authorities);
    }
}
