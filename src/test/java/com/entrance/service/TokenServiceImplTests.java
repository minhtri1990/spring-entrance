package com.entrance.service;

import com.entrance.constant.Role;
import com.entrance.dto.request.RefreshTokenRequest;
import com.entrance.dto.request.SignInRequest;
import com.entrance.dto.response.RefreshTokenResponse;
import com.entrance.entity.Token;
import com.entrance.entity.User;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@SpringBootTest
class TokenServiceImplTests {
    private TokenService serviceUnderTest;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    public void setup() {
        serviceUnderTest = new TokenServiceImpl(tokenRepository, userRepository, jwtUtil);
        // setup for SecurityContext
        authentication = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    public void when_logout_GivenValidateToken_thenInvalidRefreshTokenOfUser() {
        //stubs
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("email@gmail.com");
        User expected = User.builder()
                .id(1)
                .email("email@gmail.com")
                .build();
        //Stubs
        Mockito.when(userRepository.findByEmailEquals(expected.getEmail()))
                .thenReturn(Optional.of(expected));
        // Call method
        serviceUnderTest.logout();
        //Then
        Mockito.verify(tokenRepository, Mockito.timeout(10)).deleteByUserIdEquals(expected.getId());
    }

    @Test
    public void when_logout_GivenInvalidateToken_thenThrowException() {
        //stubs
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("");
        // Call method
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.logout();
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The refreshToken is invalid"));
    }

    @Test
    public void when_refreshToken_GivenRefreshTokenIsEmpty_thenThrowException() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("")
                .build();
        // Call method
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.refreshToken(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The refreshToken is not permitted empty"));
    }

    @Test
    public void when_refreshToken_GivenRefreshTokenIsNotExist_thenThrowException() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();
        // Call method
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.refreshToken(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The refreshToken is invalid"));
    }

    @Test
    public void when_refreshToken_GivenRefreshUserIsNotFound_thenThrowException() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();
        Token expected = Token.builder()
                .userId(1)
                .refreshToken("refreshToken")
                .build();
        //Stubs
        Mockito.when(tokenRepository.findByRefreshTokenEquals(request.getRefreshToken()))
                .thenReturn(expected);
        // Call method
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            serviceUnderTest.refreshToken(request);
        }, "Exception was expected");

        //Then
        Assertions.assertTrue(thrown.getMessage().contains("The user is not exist"));
    }

    @Test
    public void when_refreshToken_GivenRefreshUserIsExist_thenReturnNewToken() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("refreshToken")
                .build();
        Token refreshToken = Token.builder()
                .userId(1)
                .refreshToken("refreshToken")
                .build();
        User user = User.builder()
                .id(1)
                .email("email@gmail.com")
                .build();
        String newToken = "newToken";
        String newRefreshToken = "newRefreshToken";
        //Stubs
        Mockito.when(tokenRepository.findByRefreshTokenEquals(request.getRefreshToken()))
                .thenReturn(refreshToken);
        Mockito.when(userRepository.findByIdEquals(refreshToken.getUserId()))
                .thenReturn(Optional.of(user));
        Mockito.when(jwtUtil.generateToken(user, Role.USER))
                .thenReturn(newToken);
        Mockito.when(jwtUtil.doGenerateRefreshToken(user, Role.USER))
                .thenReturn(newRefreshToken);
        // Call method
        RefreshTokenResponse actual= serviceUnderTest.refreshToken(request);

        //Then
        Assertions.assertEquals(actual.getRefreshToken(),newRefreshToken);
        Assertions.assertEquals(actual.getToken(),newToken);
        Mockito.verify(tokenRepository, Mockito.timeout(10)).save(refreshToken);
    }

    @Test
    public void when_storeRefreshToken_GivenRefreshTokenIsExist_thenUpdateToken() {
        User user = User.builder()
                .id(1)
                .email("email@gmail.com")
                .build();
        Token refreshToken = Token.builder()
                .userId(1)
                .refreshToken("refreshToken")
                .build();
        String tokenRefresh = "refreshToken";
        //Stubs
        Mockito.when(tokenRepository.findByUserIdEquals(user.getId()))
                .thenReturn(refreshToken);
        Mockito.when(tokenRepository.save(refreshToken))
                .thenReturn(refreshToken);
        Token token = serviceUnderTest.storeRefreshToken(user,tokenRefresh);

        //Then
        Assertions.assertEquals(token.getRefreshToken(),tokenRefresh);
        Mockito.verify(tokenRepository, Mockito.timeout(10)).save(refreshToken);
    }

    @Test
    public void when_storeRefreshToken_GivenRefreshTokenIsNotExist_thenInsertToken() {
        User user = User.builder()
                .id(1)
                .email("email@gmail.com")
                .build();
        Token refreshToken = Token.builder()
                .userId(1)
                .refreshToken("refreshToken")
                .build();
        String tokenRefresh = "refreshToken";
        //Stubs
        Mockito.when(tokenRepository.findByUserIdEquals(user.getId()))
                .thenReturn(null);
        Mockito.when(tokenRepository.save(ArgumentMatchers.any(Token.class)))
                .thenReturn(refreshToken);
        Token token = serviceUnderTest.storeRefreshToken(user,tokenRefresh);

        //Then
        Assertions.assertEquals(token.getRefreshToken(),tokenRefresh);
        Mockito.verify(tokenRepository, Mockito.timeout(10)).save(ArgumentMatchers.any(Token.class));
    }
}
