package com.entrance.service;

import com.entrance.dto.data.UserDTO;
import com.entrance.dto.request.SignInRequest;
import com.entrance.dto.request.SignUpRequest;
import com.entrance.dto.response.AuthenticationResponse;
import com.entrance.entity.User;

import java.util.Optional;

public interface UserService {
    AuthenticationResponse login(SignInRequest request);

    UserDTO register(SignUpRequest user);
}
