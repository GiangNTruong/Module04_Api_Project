package ra.project_api.dto.response;

import lombok.Data;
import ra.project_api.dto.request.RoleDTO;

import java.util.Date;
import java.util.Set;

@Data
public class UserResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Boolean status;
    private String phone;
    private String address;
    private Date createdAt;
    private Date updatedAt;
    private Set<RoleDTO> roles;
}