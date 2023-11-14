package chunjae.api.domain.entity.home;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

// import java.util.ArrayList;
// import java.util.List;

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "TBL_LCMS_Notice")
@Entity
public class LcmsNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Idx")
    private Integer idx;

    @ColumnDefault("''")
    @Column(name = "SubjectCode", columnDefinition = "varchar", length = 3)
    private String subjectCode;

    @ColumnDefault("0")
    @Column(name = "Lecturer_Code")
    private int lecturerCode;

    @Column(name = "UserID", columnDefinition = "varchar", length = 50)
    private String userID;

    @Column(name = "Title", columnDefinition = "varchar", length = 100)
    private String title;

    @Column(name = "Content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "Attach_File1", columnDefinition = "varchar", length = 200)
    private String attachFile1;

    @Column(name = "Attach_File2", columnDefinition = "varchar", length = 200)
    private String attachFile2;
    
    @ColumnDefault("'Y'")
    @Column(name = "VisibleYN", columnDefinition = "char", length = 1)
    private char visibleYN;
    
    @ColumnDefault("'N'")
    @Column(name = "DeleteYN", columnDefinition = "char", length = 1)
    private char deleteYN;
    
    @ColumnDefault("'N'")
    @Column(name = "TopYN", columnDefinition = "char", length = 1)
    private char topYN;
    
    @Column(name = "RegDate", columnDefinition = "datetime")
    private LocalDateTime regDate;
    
    @Column(name = "InputDate", columnDefinition = "varchar", length = 10)
    private String inputDate;
    
    @ColumnDefault("0")
    @Column(name = "Platform", columnDefinition = "tinyint")
    private int platform;
}
