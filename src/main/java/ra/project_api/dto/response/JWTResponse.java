package ra.project_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JWTResponse {
    private String username;
    private String fullName;
    private Collection<? extends GrantedAuthority> roles;
    private final String type= "Bearer";
    private String accessToken;
    private String refreshToken;
}
