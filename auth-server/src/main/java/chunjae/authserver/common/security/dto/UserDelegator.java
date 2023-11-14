package chunjae.authserver.common.security.dto;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


// 참조
// jdt://contents/spring-security-core-6.0.2.jar/org.springframework.security.core.userdetails/User.class

public interface UserDelegator extends UserDetails{
    // roles와 singleAuthorites에서 권한들을 가져온다.
    Collection<? extends GrantedAuthority> getAuthorities(Collection<? extends GrantedAuthority> authorities);
}
