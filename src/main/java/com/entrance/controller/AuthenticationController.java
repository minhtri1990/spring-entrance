package com.entrance.controller;

import com.entrance.dto.data.ResponseBuilder;
import com.entrance.dto.data.UserDTO;
import com.entrance.dto.request.RefreshTokenRequest;
import com.entrance.dto.request.SignInRequest;
import com.entrance.dto.request.SignUpRequest;
import com.entrance.dto.response.AuthenticationResponse;
import com.entrance.dto.response.RefreshTokenResponse;
import com.entrance.security.JwtUtil;
import com.entrance.service.TokenService;
import com.entrance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@APIV1Controller
@RestController
public class AuthenticationController {
    private final UserService userService;
    private final TokenService tokenService;

    @Operation(description = "sign-up")
    @PostMapping(value = "/sign-up")
    public ResponseEntity register(@RequestBody SignUpRequest request) {
        UserDTO user = userService.register(request);
        return ResponseBuilder
                .buildResponse(HttpStatus.OK.value(), "register success", user);
    }

    @Operation(description = "sign-in")
    @PostMapping(value = "/sign-in")
    public ResponseEntity login(@RequestBody SignInRequest request) {
        AuthenticationResponse response = userService.login(request);
        return ResponseBuilder.buildResponse(HttpStatus.OK.value(), "login success", response);
    }

    @Operation(description = "sign-out")
    @PostMapping(value = "/sign-out")
    public ResponseEntity logout() {
        tokenService.logout();
        return ResponseBuilder
                .buildResponse(HttpStatus.OK.value(), "logout success", true);
    }

    @Operation(description = "refresh-token")
    @PostMapping(value = "/refresh-token")
    public ResponseEntity refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = tokenService.refreshToken(request);
        return ResponseBuilder
                .buildResponse(HttpStatus.OK.value(), "refresh-token success", response);
    }
}
