package ra.project_api.dto.request;

import lombok.Data;
import ra.project_api.constrants.RoleName;

@Data
public class RoleDTO {
    private Long roleId;
    private RoleName roleName;
}