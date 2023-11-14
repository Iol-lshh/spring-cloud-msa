package chunjae.authserver.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import chunjae.authserver.common.security.dto.UserTokenDto;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "TBL_WWW_Member_Token_TEMP")
@Entity
public class UserToken {
    @Id
    @Column(name = "refresh", columnDefinition = "varchar", length = 300)
    private String refresh;

    @Column(name = "access", columnDefinition = "varchar", length = 300)
    private String access;

    // 중복 로그인 수 확인용
    @Column(name = "UserID", columnDefinition = "varchar", length = 50)
    private String username;
    
    @Column(name = "RegDate", columnDefinition = "datetime")
    private LocalDateTime regDate;
    
    @ColumnDefault("'N'")
    @Column(name = "DelYn", columnDefinition = "char", length = 1)
    private char deleteYN;


    // ip가 다를시, 확인하는 로직
    // private String ip;

    public boolean isNotDeleted(){
        return this.deleteYN != 'Y';
    }

    public UserTokenDto toDto(){
        return UserTokenDto.builder()
                    .refresh(this.refresh)
                    .access(this.access)
                    .username(this.username)
                    .isNotDeleted(this.isNotDeleted())
                    .build();
    }
}
