package ra.project_api.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Old password must not be blank")
    private String oldPass;

    @NotBlank(message = "New password must not be blank")
    private String newPass;

    @NotBlank(message = "Confirm new password must not be blank")
    private String confirmNewPass;
}