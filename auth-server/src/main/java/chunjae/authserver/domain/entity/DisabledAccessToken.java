package chunjae.authserver.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

// 일정 시각마다, 만료된 AccessToken들을 delete 시킬 것
@Accessors(chain = true)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "TBL_WWW_Member_Disabled_Access_Token_TEMP")
@Entity
public class DisabledAccessToken {
    @Id
    @Column(name = "access", columnDefinition = "varchar", length = 300)
    private String access;

    @Column(name = "RegDate", columnDefinition = "datetime")
    private LocalDateTime regDate;
}
