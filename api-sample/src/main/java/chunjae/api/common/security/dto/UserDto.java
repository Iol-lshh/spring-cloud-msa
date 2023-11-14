package chunjae.api.common.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class UserDto {
    private String username;
    // private byte[] password;
    private String password;
}
