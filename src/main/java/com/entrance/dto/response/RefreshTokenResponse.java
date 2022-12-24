package com.entrance.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshTokenResponse {
    private String refreshToken;
    private String token;

}
