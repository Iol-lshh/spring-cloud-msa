package chunjae.authserver.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import chunjae.authserver.domain.entity.UserToken;


public interface UserTokenRepository extends JpaRepository<UserToken, String>{
    Optional<UserToken> findUserTokenByRefreshAndDeleteYN(String refresh, char deleteYN);

    List<UserToken> findListByUsernameAndDeleteYN(String username, char deleteYN);
}
