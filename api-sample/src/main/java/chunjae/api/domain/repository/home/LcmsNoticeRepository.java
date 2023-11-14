package chunjae.api.domain.repository.home;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import chunjae.api.domain.entity.home.LcmsNotice;

public interface LcmsNoticeRepository extends JpaRepository<LcmsNotice, Integer>{
    Optional<LcmsNotice> findLcmsNoticeByIdx(int idx);
}
