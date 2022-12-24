package com.entrance.service;

import com.entrance.constant.Role;
import com.entrance.dto.data.UserDTO;
import com.entrance.dto.request.SignInRequest;
import com.entrance.dto.request.SignUpRequest;
import com.entrance.dto.response.AuthenticationResponse;
import com.entrance.entity.User;
import com.entrance.exception.BadRequestException;
import com.entrance.exception.ResourceNotFoundException;
import com.entrance.exception.ServerErrorException;
import com.entrance.repository.UserRepository;
import com.entrance.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final JwtUtil jwtUtil;

    @Override
    public AuthenticationResponse login(SignInRequest request) {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            throw new BadRequestException("The email and password is not match");
        }

        Optional<User> user = getUserByEmail(request.getEmail());
        if (user.isEmpty()) {
            // case happen when error database
            throw new ResourceNotFoundException("The user is not exist");
        }

        String token = jwtUtil.generateToken(user.get(), Role.USER);
        String refreshToken = jwtUtil.doGenerateRefreshToken(user.get(), Role.USER);
        tokenService.storeRefreshToken(user.get(), refreshToken);
        return AuthenticationResponse
                .builder()
                .user(UserDTO.convert(user.get()))
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public UserDTO register(SignUpRequest request) {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        // validate available im database
        userRepository.findByEmailEquals(request.getEmail()).ifPresent(user -> {
            throw new BadRequestException(String.format("The email %s have existed", user.getEmail()));
        });
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(jwtUtil.encode(request.getPassword()))
                .build();
        User store = userRepository.save(user);
        return UserDTO.convert(store);
    }

    private Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailEquals(email);
    }

    private void validateEmail(String email) {
        //validate email format
        boolean isValidEmail = EmailValidator.getInstance().isValid(email);
        if (!isValidEmail) {
            throw new BadRequestException(String.format("The email %s is wrong format", email));
        }
    }

    private void validatePassword(String password) {
        //validate between 8-20 characters
        if (StringUtils.isBlank(password)) {
            throw new BadRequestException("The password is not permitted empty");
        }
        if (password.length() < 8 || password.length() > 20) {
            throw new BadRequestException("The password must be between 8-20 characters");
        }
    }
}
