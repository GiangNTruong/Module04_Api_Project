package ra.project_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ra.project_api.dto.request.SignInRequest;
import ra.project_api.dto.request.SignUpRequest;
import ra.project_api.service.IUserService;
///api.myservice.com/v1/auth/sign-up
@RestController
@RequiredArgsConstructor
@RequestMapping("/api.myservice.com/v1/auth")
public class AuthController {
    private final IUserService userService;
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest){
        userService.signUp(signUpRequest);
        return ResponseEntity.ok("Success");
    }
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInRequest signInRequest){
        return ResponseEntity.ok(userService.signIn(signInRequest));
    }
}
