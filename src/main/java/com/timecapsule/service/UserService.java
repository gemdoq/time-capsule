package com.timecapsule.service;

import com.timecapsule.dto.request.ChangePasswordRequest;
import com.timecapsule.dto.request.UpdateProfileRequest;
import com.timecapsule.dto.response.MessageResponse;
import com.timecapsule.dto.response.UserResponse;
import com.timecapsule.entity.User;
import com.timecapsule.exception.CustomException;
import com.timecapsule.exception.ErrorCode;
import com.timecapsule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setNickname(request.getNickname());
        return UserResponse.from(user);
    }

    @Transactional
    public MessageResponse changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return MessageResponse.of("비밀번호가 변경되었습니다.");
    }
}
