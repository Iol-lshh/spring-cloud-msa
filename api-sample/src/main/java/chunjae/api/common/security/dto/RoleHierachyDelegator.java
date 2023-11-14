package chunjae.api.common.security.dto;

import java.util.Collection;

import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;

public interface RoleHierachyDelegator extends RoleHierarchy{

    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities();    
}
