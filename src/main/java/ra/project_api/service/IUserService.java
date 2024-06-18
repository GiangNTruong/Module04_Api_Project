package ra.project_api.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.project_api.dto.request.ChangePasswordRequest;
import ra.project_api.dto.request.SignInRequest;
import ra.project_api.dto.request.SignUpRequest;
import ra.project_api.dto.response.JWTResponse;
import ra.project_api.model.Role;
import ra.project_api.model.User;
import java.util.List;
import java.util.Optional;

public interface IUserService {

    void signUp(SignUpRequest signUpRequest);
    JWTResponse signIn(SignInRequest signInRequest);
    Page<User> getUsers(Pageable pageable);
    Page<User> searchUsersByUsername(String username, Pageable pageable);
    List<Role> getRoles();
    User findUserById(Long id);
    User updateUserStatus(Long userId);
    void changePassword(Long userId, ChangePasswordRequest changePasswordRequest);
    Optional<User> findByUsernameOrEmail(String username, String email);
    User updateUser(User user);

}
