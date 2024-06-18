package ra.project_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.project_api.validation.PasswordMatching;

@AllArgsConstructor
@NoArgsConstructor
@Data
@PasswordMatching(
        password = "newPass",
        confirmPassword = "confirmNewPass"
)
public class ChangePasswordRequest {
    private String oldPass;
    private String newPass;
    private String confirmNewPass;
}