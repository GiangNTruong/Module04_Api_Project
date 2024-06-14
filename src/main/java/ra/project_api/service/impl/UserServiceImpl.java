package ra.project_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.project_api.constrants.RoleName;
import ra.project_api.dto.request.SignInRequest;
import ra.project_api.dto.request.SignUpRequest;
import ra.project_api.dto.response.JWTResponse;
import ra.project_api.model.Role;
import ra.project_api.model.User;
import ra.project_api.repository.IRoleRepository;
import ra.project_api.repository.IUserRepository;
import ra.project_api.security.jwt.JwtProvider;
import ra.project_api.security.principle.CustomerUserDetail;
import ra.project_api.service.IUserService;


import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;



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
}
