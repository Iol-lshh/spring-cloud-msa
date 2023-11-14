package chunjae.authserver.common.security.dto;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import lombok.RequiredArgsConstructor;

// 참조
// jdt://contents/spring-security-core-6.0.2.jar/org.springframework.security.core.userdetails/User.class
@RequiredArgsConstructor
public class UserDelegatorImpl implements UserDelegator{
    private UserDto userDto;
    
    // user의 토큰 정보
    // private UserToken userToken;

    // user의 roles 정보 (계층 구조) 
    private List<RoleHierachyDelegator> roles;

    // user의 role에 포함되지 않은 authority 정보 
    private List<GrantedAuthority> singleAuthorites;

    public UserDelegatorImpl(UserDto userDto) {
        this.userDto = userDto;
        // this.roles = user.roles; - user에서 lazy하게 조인하여 끌고와도 된다!
        // this.singleAuthorites = user.singleAuthorites; - user에서 lazy하게 조인하여 끌고와도 된다!
    }
    // 이렇게 구현하는게 자유도가 더 높아 보인다.
    // public UserDelegatorImpl setSingleAuthorites(SingleAuthoritesRepository singleAuthoritesRepository){
    //     // this.singleAuthorites를 entity와 별개로 가공하는 전처리가 가능해진다!
    //     this.singleAuthorites = singleAuthoritesRepository.findByUser(this.user);
    //     return this;
    // }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // roles와 singleAuthorites에서 권한들을 가져온다.
        Set<GrantedAuthority> results = new HashSet<>();
        // TODO RoleHierachyDelegatorImpl 구현
        roles.forEach(role -> role.getReachableGrantedAuthorities()
                                    .forEach(auth -> results.add(auth)));
        results.addAll(this.singleAuthorites);        
        return results;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(Collection<? extends GrantedAuthority> authorities) {
        // roles와 singleAuthorites에서 권한들을 가져온다.
        Set<GrantedAuthority> results = new HashSet<>();
        // TODO RoleHierachyDelegatorImpl 구현
        this.roles.forEach(role -> role.getReachableGrantedAuthorities(authorities)
                                    .forEach(auth -> results.add(auth)));
        authorities.stream().filter(requestAuth -> this.singleAuthorites.contains(requestAuth))
                            .forEach(filteredAuth -> results.add(filteredAuth));
        return results;
    }

    @Override
    public String getPassword() {
        // return Base64.getEncoder().encodeToString(this.user.getPassword());
        return this.userDto.getPassword();
    }

    @Override
    public String getUsername() {
        return this.userDto.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO 계정 만료(토큰)
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO 계정 잠김
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO 비밀번호 만료
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 유저 활성화 여부
        // TODO 탈퇴 && 계정 만료 && 토큰 만료 && 사이트 권한 확인
        return true;
    }


}
