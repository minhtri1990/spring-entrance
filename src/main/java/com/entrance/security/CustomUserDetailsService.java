package com.entrance.security;

import com.entrance.constant.Role;
import com.entrance.entity.User;
import com.entrance.exception.ResourceNotFoundException;
import com.entrance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> roles = null;
        System.out.println("userRepository "+ userRepository.hashCode());
        Optional<User> user = userRepository.findByEmailEquals(email);
        if (user.isPresent()) {
            roles = Arrays.asList(new SimpleGrantedAuthority(Role.USER));
            return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(), roles);
        }
        throw new ResourceNotFoundException("User not found with the email " + email);
    }
}
