package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.UpdateProfileRequest;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
public User updateProfile(String email, UpdateProfileRequest req) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (req.getFirstName() != null && !req.getFirstName().isBlank())
        user.setFirstName(req.getFirstName());

    if (req.getLastName() != null && !req.getLastName().isBlank())
        user.setLastName(req.getLastName());

    if (req.getPhone() != null && !req.getPhone().isBlank())
        user.setPhone(req.getPhone());

    if (req.getAvatarUrl() != null)
        user.setAvatarUrl(req.getAvatarUrl());

    return userRepository.save(user);
}

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}