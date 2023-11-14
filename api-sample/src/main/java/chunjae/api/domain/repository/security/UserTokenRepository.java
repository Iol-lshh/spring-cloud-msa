package chunjae.api.domain.repository.security;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import chunjae.api.domain.entity.security.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, String>{
    Optional<UserToken> findUserTokenByRefreshAndDeleteYN(String refresh, char deleteYN);

    List<UserToken> findListByUsernameAndDeleteYN(String username, char deleteYN);
}
