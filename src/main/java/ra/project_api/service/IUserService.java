package ra.project_api.service;


import ra.project_api.dto.request.SignInRequest;
import ra.project_api.dto.request.SignUpRequest;
import ra.project_api.dto.response.JWTResponse;

import java.util.List;

public interface IUserService {

    void signUp(SignUpRequest signUpRequest);
    JWTResponse signIn(SignInRequest signInRequest);
}
