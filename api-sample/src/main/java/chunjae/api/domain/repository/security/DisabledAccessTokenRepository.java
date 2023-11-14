package chunjae.api.domain.repository.security;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import chunjae.api.domain.entity.security.DisabledAccessToken;

public interface DisabledAccessTokenRepository extends JpaRepository<DisabledAccessToken, String>{
    void deleteByAccess (String access);

    @Modifying
    @Query("DELETE FROM DisabledAccessToken DAT WHERE DAT.regDate < :checkDateTime")
    void deleteAllLessThan(@Param("checkDateTime") LocalDateTime chekDateTime);
}
