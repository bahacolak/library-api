package com.bahadircolak.library.service;

import com.bahadircolak.library.config.JwtService;
import com.bahadircolak.library.config.PasswordEncoderService;
import com.bahadircolak.library.model.Role;
import com.bahadircolak.library.model.User;
import com.bahadircolak.library.repository.UserRepository;
import com.bahadircolak.library.web.request.AuthenticationRequest;
import com.bahadircolak.library.web.request.RegisterRequest;
import com.bahadircolak.library.web.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoderService passwordEncoderService;

    public AuthenticationResponse register(RegisterRequest request) {

        String salt = passwordEncoderService.generateSalt();
        String hashedPassword = passwordEncoderService.hash(request.getPassword(), salt);

        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(hashedPassword)
                .salt(salt)
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(request.getPassword() + user.getSalt(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
