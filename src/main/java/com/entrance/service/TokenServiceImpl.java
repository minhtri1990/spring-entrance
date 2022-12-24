package com.entrance.service;

import com.entrance.constant.Role;
import com.entrance.dto.request.RefreshTokenRequest;
import com.entrance.dto.response.RefreshTokenResponse;
import com.entrance.entity.Token;
import com.entrance.entity.User;
import com.entrance.exception.BadRequestException;
import com.entrance.exception.ResourceNotFoundException;
import com.entrance.exception.ServerErrorException;
import com.entrance.repository.TokenRepository;
import com.entrance.repository.UserRepository;
import com.entrance.security.JwtUtil;
import com.entrance.security.SecurityUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void logout() {
        String email = SecurityUtils.getCurrentUser();
        if (StringUtils.isBlank(email)) {
            throw new ResourceNotFoundException("The refreshToken is invalid");
        }
        userRepository.findByEmailEquals(email)
                .ifPresent(this::invalidRefreshTokenOfUser);
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        if (StringUtils.isBlank(request.getRefreshToken())) {
            throw new BadRequestException("The refreshToken is not permitted empty");
        }

        Token refreshToken = tokenRepository.findByRefreshTokenEquals(request.getRefreshToken());
        if (Objects.isNull(refreshToken)) {
            throw new ResourceNotFoundException("The refreshToken is invalid");
        }

        Optional<User> user = userRepository.findByIdEquals(refreshToken.getUserId());
        if (user.isEmpty()) {
            // user is deleted.
            throw new ResourceNotFoundException("The user is not exist");
        }

        String newToken = jwtUtil.generateToken(user.get(), Role.USER);
        String newRefreshToken = jwtUtil.doGenerateRefreshToken(user.get(), Role.USER);

        // store RefreshToken
        refreshToken.setRefreshToken(newRefreshToken);
        refreshToken.setUpdatedAt(LocalDateTime.now());
        tokenRepository.save(refreshToken);

        return RefreshTokenResponse.builder()
                .refreshToken(newRefreshToken)
                .token(newToken)
                .build();
    }

    @Override
    public Token storeRefreshToken(User user, String refreshToken) {
        Token token = tokenRepository.findByUserIdEquals(user.getId());
        if (Objects.nonNull(token)) {
            token.setRefreshToken(refreshToken);
            token.setUpdatedAt(LocalDateTime.now());
            return tokenRepository.save(token);
        }
        //store token
        Token newToken = Token.builder()
                .expiresIn("7d")
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();

        return tokenRepository.save(newToken);
    }

    private long invalidRefreshTokenOfUser(User user) {
        return tokenRepository.deleteByUserIdEquals(user.getId());
    }
}
