package com.entrance.service;

import com.entrance.dto.data.UserDTO;
import com.entrance.dto.request.SignInRequest;
import com.entrance.dto.request.SignUpRequest;
import com.entrance.dto.response.AuthenticationResponse;
import com.entrance.entity.User;
import com.entrance.exception.BadRequestException;
import com.entrance.repository.TokenRepository;
import com.entrance.repository.UserRepository;
import com.entrance.security.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

@SpringBootTest
class UserServiceImplTests {
    private UserServiceImpl serviceUnderTest;
    private TokenService tokenService;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        tokenService = new TokenServiceImpl(tokenRepository, userRepository, jwtUtil);
        serviceUnderTest = new UserServiceImpl(userRepository, authenticationManager, tokenService, jwtUtil);
    }

    @Test
    public void when_login_GivenUserEmptyEmail_thenThrowException() {
        // Given
        SignInRequest request = SignInRequest.builder()
                .email("")
                .build();
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.login(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains(String.format("The email %s is wrong format", request.getEmail())));
    }

    @Test
    public void when_login_GivenUserEmptyPassword_thenThrowException() {
        // Given
        SignInRequest request = SignInRequest.builder()
                .email("email@gmail.com")
                .password("")
                .build();
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.login(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The password is not permitted empty"));
    }

    @Test
    public void when_login_GivenUserInvalidPasswordLengthLessThan8_thenThrowException() {
        // Given
        SignInRequest request = SignInRequest.builder()
                .email("email@gmail.com")
                .password("1234567")
                .build();
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.login(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The password must be between 8-20 characters"));
    }

    @Test
    public void when_login_GivenUserInvalidPasswordLongerThan20_thenThrowException() {
        // Given
        SignInRequest request = SignInRequest.builder()
                .email("email@gmail.com")
                .password("123456789012345678901")
                .build();
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.login(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The password must be between 8-20 characters"));
    }

    @Test
    public void when_login_GivenUserPasswordNotMatch_thenThrowException() {
        // Given
        SignInRequest request = SignInRequest.builder()
                .email("email@gmail.com")
                .password("12345678")
                .build();
        //Stubs
        Mockito.when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadRequestException("The email and password is not match"));

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.login(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The email and password is not match"));
    }

    @Test
    public void when_login_GivenUserIsDeleted_thenThrowException() {
        // Given
        SignInRequest request = SignInRequest.builder()
                .email("email@gmail.com")
                .password("12345678")
                .build();
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.login(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The user is not exist"));
    }

    @Test
    public void when_login_GivenUserIsExist_thenLoginSuccess() {
        // Given
        SignInRequest request = SignInRequest.builder()
                .email("email@gmail.com")
                .password("12345678")
                .build();
        User expected = User.builder()
                .email("email@gmail.com")
                .build();
        //Stubs
        Mockito.when(userRepository.findByEmailEquals(request.getEmail()))
                .thenReturn(Optional.of(expected));
        // Call method
        AuthenticationResponse response = serviceUnderTest.login(request);
        //Then
        Assertions.assertEquals(response.getUser().getEmail(),expected.getEmail());
    }

    @Test
    public void when_register_GivenUserEmptyEmail_thenThrowException() {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("")
                .build();
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.register(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains(String.format("The email %s is wrong format", request.getEmail())));
    }

    @Test
    public void when_register_GivenUserEmptyPassword_thenThrowException() {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("email@gmail.com")
                .password("")
                .build();
        // Call method
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.register(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The password is not permitted empty"));
    }

    @Test
    public void when_register_GivenUserInvalidPassword_thenThrowException() {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("email@gmail.com")
                .password("123")
                .build();
        // Call method
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.register(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The password must be between 8-20 characters"));
    }

    @Test
    public void when_register_GivenUserEmailIsExist_thenThrowException() {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("email@gmail.com")
                .password("12345678")
                .build();
        User expected = User.builder()
                .email("email@gmail.com")
                .build();
        //Stubs
        Mockito.when(userRepository.findByEmailEquals(request.getEmail()))
                .thenReturn(Optional.of(expected));

        // Call method
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.register(request);
        }, "Exception was expected");
        //Then
        Assertions.assertTrue(thrown.getMessage().contains(String.format("The email %s have existed", request.getEmail())));
    }

    @Test
    public void when_register_GivenUserEmailIsNotExist_thenRegisterSuccess() {
        // Given
        SignUpRequest request = SignUpRequest.builder()
                .email("email@gmail.com")
                .password("12345678")
                .build();
        User expected = User.builder()
                .email("email@gmail.com")
                .build();
        //Stubs
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(expected);
        // Call method
        UserDTO actual =serviceUnderTest.register(request);
        //Then
        Assertions.assertEquals(actual.getEmail(),request.getEmail());
    }
}
