package ra.project_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.project_api.validation.EmailUnique;
import ra.project_api.validation.PhoneUnique;
import ra.project_api.validation.UserNameUnique;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignUpRequest {
    @NotBlank
    @UserNameUnique
    private String username;
    @NotBlank
    @EmailUnique
    private String email;
    private String fullName;
    private Boolean status;
    private String password;
    @NotBlank
    @PhoneUnique
    private String phone;
    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private Date updatedAt;
    private List<String> roleList;
}
