package com.entrance.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SignUpRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;

}
