package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.LoginRequest;
import com.bagygo.bagygo_backend.dto.request.RegisterRequest;
import com.bagygo.bagygo_backend.dto.response.AuthResponse;
import com.bagygo.bagygo_backend.dto.response.UserResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.UserRole;
import com.bagygo.bagygo_backend.repository.UserRepository;
import com.bagygo.bagygo_backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Find or create the role
        UserRole role = request.getRole();

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .rating(0.0)
                .transportDocumentUrl(request.getTransportDocumentUrl())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.from(user))
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check restrictions for TRANSPORTEUR
        if (user.getRole() == UserRole.TRANSPORTEUR) {
            if (Boolean.TRUE.equals(user.getIsBanned())) {
                throw new RuntimeException("Your account has been banned by the admin.");
            }
            if (!Boolean.TRUE.equals(user.getIsVerified())) {
                throw new RuntimeException("Your account must be verified first.");
            }
        }

        String token = jwtUtil.generateToken(request.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.from(user))
                .message("Login successful")
                .build();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}