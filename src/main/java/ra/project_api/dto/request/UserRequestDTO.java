package ra.project_api.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDTO {
    private String email;
    private String fullname;
    private String phone;
    private String address;
}