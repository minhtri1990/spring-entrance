package com.entrance.service;

import com.entrance.dto.request.RefreshTokenRequest;
import com.entrance.dto.response.RefreshTokenResponse;
import com.entrance.entity.Token;
import com.entrance.entity.User;

public interface TokenService {

    void logout();

    Token storeRefreshToken(User user, String refreshToken);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

}
