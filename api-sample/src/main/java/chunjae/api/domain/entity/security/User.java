package chunjae.api.domain.entity.security;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import chunjae.api.common.security.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
// @Table(name = "TBL_WWW_Member")
@Table(name = "TBL_WWW_Member_TEMP")
@Entity
public class User{
    @Id
    @Column(name = "UserID", columnDefinition = "varchar", length = 50)
    private String username;
    
    // @Column(name = "Pwd3", columnDefinition = "varbinary", length = 128)
    // private byte[] password;
    @Column(name = "Pwd", columnDefinition = "varchar", length = 20)
    private String password;

    @Column(name = "RegDate", columnDefinition = "datetime")
    private LocalDateTime regDate;

    // 회원가입을 위해서 임의로 적어둠..
    /*
     * ChunjaeUserID
     * UserType
     * UserID --
     * Pwd
     * UserName
     * Lunar
     * MemberType
     * Mailing1
     * SMS
     * Ticket
     * RecommendCode    //default 0
     * MemberClass      //default '6'
     * FreeYN           //default 'N'
     * DelYn            //default 'N'
     * RegDate          //default getdate()
     * MktAllow         //default 있음 x
     * KingFaceYN       //default 있음 x
     */
    @Column(name = "ChunjaeUserID", columnDefinition = "char", length = 9)
    private String a1;
    @Column(name = "UserType", columnDefinition = "char", length = 1)
    private String a2;
    // @Column(name = "Pwd", columnDefinition = "varchar", length = 20)
    // private String a3;
    @Column(name = "UserName", columnDefinition = "varchar", length = 9)
    private String a4;
    @Column(name = "Lunar", columnDefinition = "char", length = 1)
    private String a5;
    @Column(name = "MemberType", columnDefinition = "char", length = 2)
    private String a6;
    @Column(name = "Mailing1", columnDefinition = "tinyint")
    private int a7;
    @Column(name = "SMS", columnDefinition = "tinyint")
    private int a8;
    @Column(name = "Ticket", columnDefinition = "tinyint")
    private int a9;

    @Column(name = "RecommendCode", columnDefinition = "char", length = 1)
    private String a10;
    @Column(name = "MemberClass", columnDefinition = "varchar", length = 3)
    private String a11;
    @Column(name = "FreeYN", columnDefinition = "char", length = 1)
    private String a12;
    @Column(name = "DelYn", columnDefinition = "char", length = 10)
    private String a13;

    public UserDto toDto(){
        return UserDto.builder()
                    .username(this.username)
                    .password(this.password)
                    .build();
    }
}
