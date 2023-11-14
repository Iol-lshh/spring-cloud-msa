package chunjae.authserver.common.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class UserTokenDto {
    private String refresh;
    private String access;
    private String username;
    private boolean isNotDeleted;
}
