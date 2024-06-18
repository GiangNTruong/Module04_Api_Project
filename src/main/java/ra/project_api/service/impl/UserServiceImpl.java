package ra.project_api.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import ra.project_api.constrants.RoleName;
import ra.project_api.dto.request.ChangePasswordRequest;
import ra.project_api.dto.request.SignInRequest;
import ra.project_api.dto.request.SignUpRequest;
import ra.project_api.dto.request.UserRequestDTO;
import ra.project_api.dto.response.JWTResponse;
import ra.project_api.dto.response.UserResponseDTO;
import ra.project_api.model.Role;
import ra.project_api.model.User;
import ra.project_api.model.WishList;
import ra.project_api.repository.IRoleRepository;
import ra.project_api.repository.IUserRepository;
import ra.project_api.repository.WishListRepository;
import ra.project_api.security.jwt.JwtProvider;
import ra.project_api.security.principle.CustomerUserDetail;
import ra.project_api.service.IUserService;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;
    private final WishListRepository wishListRepository;


    @Override
    public void signUp(SignUpRequest signUpRequest) {
        Set<Role> roleSet = new HashSet<>();
        if (signUpRequest.getRoleList() == null || signUpRequest.getRoleList().isEmpty() ) {
            roleSet.add(roleRepository.findByRoleName(RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("Không tìm thấy role")));
        } else {
            signUpRequest.getRoleList().forEach(s -> {
                switch (s) {
                    case "admin":
                        roleSet.add(roleRepository.findByRoleName(RoleName.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Không tìm thấy role")));
                    case "pm":
                        roleSet.add(roleRepository.findByRoleName(RoleName.ROLE_PM).orElseThrow(() -> new RuntimeException("Không tìm thấy role")));
                    case "user":
                        roleSet.add(roleRepository.findByRoleName(RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("Không tìm thấy role")));
                        break;
                    default:
                        throw new RuntimeException("role not found");
                }
            });
        }
        User user = modelMapper.map(signUpRequest, User.class);
        user.setRoles(roleSet);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public JWTResponse signIn(SignInRequest signInRequest) {
        Authentication authentication ;
        try{
            authentication =authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInRequest.getUsername(),signInRequest.getPassword()
            ));
        }catch (Exception e){
            throw  new RuntimeException("Username or password incorrect");
        }
        CustomerUserDetail userDetail  = (CustomerUserDetail) authentication.getPrincipal();
        String accessToken = jwtProvider.generateAccessToken(userDetail);
        String refreshToken = jwtProvider.generateAccessToken(userDetail);
        return JWTResponse.builder()
                .username(userDetail.getUsername())
                .fullName(userDetail.getFullName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(userDetail.getAuthorities())
                .build();
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> searchUsersByUsername(String username, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }
    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public User findUserById(Long id)
    {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User updateUserStatus(Long userId) {
        User userRoleCheck = findUserById(userId);
        if (userRoleCheck == null)
        {
            log.error("User not found");
            throw new NoSuchElementException("Không thay đổi trạng thái được , không tồn tại id : " + userId);
        }
        if (userRoleCheck.getRoles().stream().anyMatch(role -> role.getRoleName() == RoleName.ROLE_ADMIN))
        {
            throw new RuntimeException("Can't block admin");
        }
        userRoleCheck.setStatus(!userRoleCheck.getStatus());
        userRepository.save(userRoleCheck);
        return userRoleCheck;
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPass(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (!changePasswordRequest.getNewPass().equals(changePasswordRequest.getConfirmNewPass())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPass()));
        user.setUpdatedAt(new Date());
        userRepository.save(user);
    }


    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }



}
