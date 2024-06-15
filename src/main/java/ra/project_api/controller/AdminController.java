package ra.project_api.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ra.project_api.constrants.EHttpStatus;
import ra.project_api.dto.response.ResponseWrapper;
import ra.project_api.dto.response.UserResponseDTO;
import ra.project_api.model.Role;
import ra.project_api.model.User;
import ra.project_api.service.IUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api.myservice.com/v1/admin")
public class AdminController {

    private final IUserService userService;
    private final ModelMapper modelMapper;
    @GetMapping("/users")
    public ResponseEntity<ResponseWrapper<Page<UserResponseDTO>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = userService.getUsers(pageable);

        List<UserResponseDTO> userResponseDTOs = usersPage.getContent().stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());

        Page<UserResponseDTO> userResponseDTOPage = new PageImpl<>(userResponseDTOs, pageable, usersPage.getTotalElements());

        ResponseWrapper<Page<UserResponseDTO>> responseWrapper = ResponseWrapper.<Page<UserResponseDTO>>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(userResponseDTOPage)
                .build();

        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/users/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userService.searchUsersByUsername(username, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = userService.getRoles();
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ResponseWrapper<UserResponseDTO>> updateUserStatus(@PathVariable Long userId) {
        User updatedUser = userService.updateUserStatus(userId);
        UserResponseDTO userResponseDTO = modelMapper.map(updatedUser, UserResponseDTO.class);
        ResponseWrapper<UserResponseDTO> responseWrapper = ResponseWrapper.<UserResponseDTO>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(userResponseDTO)
                .build();
        return ResponseEntity.ok(responseWrapper);
    }
}
