package chunjae.api.domain.dto.home;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class LcmsNoticeDto {
    @JsonProperty("idx")
    private Integer idx;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("UserID")
    private String userId;

    @JsonProperty("Content")
    private String content;
}