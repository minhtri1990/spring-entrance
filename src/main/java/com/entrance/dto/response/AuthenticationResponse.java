package com.entrance.dto.response;

import com.entrance.dto.data.UserDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private UserDTO user;
    private String token;
    private String refreshToken;
}
