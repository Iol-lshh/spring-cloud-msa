package chunjae.authserver.common.security.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class RoleHierachyDelegatorImpl implements RoleHierachyDelegator{

    @Override
    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
    Collection<? extends GrantedAuthority> authorities) {
        // TODO this role에서, 계층 구조로, 인자에 대하여 허용 권한 필터 반환
        throw new UnsupportedOperationException("Unimplemented method 'getReachableGrantedAuthorities'");
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities() {
        // TODO this role에서, 계층 구조로, 전체 GrantedAuthority 반환
        throw new UnsupportedOperationException("Unimplemented method 'getAllReachableGrantedAuthorities'");
    }
    
}
