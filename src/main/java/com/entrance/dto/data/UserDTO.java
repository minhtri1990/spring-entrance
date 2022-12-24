package com.entrance.dto.data;

import com.entrance.entity.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;

    public String getDisplayName() {
        return this.firstName +" "+this.firstName;
    }

    public static UserDTO convert(User dto) {
        return  UserDTO.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .build();

    }

}
