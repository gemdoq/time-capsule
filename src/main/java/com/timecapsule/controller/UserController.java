package com.timecapsule.controller;

import com.timecapsule.dto.request.ChangePasswordRequest;
import com.timecapsule.dto.request.UpdateProfileRequest;
import com.timecapsule.dto.response.MessageResponse;
import com.timecapsule.dto.response.UserResponse;
import com.timecapsule.security.CustomUserDetails;
import com.timecapsule.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateProfile(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/password")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        MessageResponse response = userService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }
}
