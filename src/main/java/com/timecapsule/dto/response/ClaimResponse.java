package com.timecapsule.dto.response;

import com.timecapsule.entity.RecipientClaim;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ClaimResponse {

    private Long id;
    private PredictionInfo prediction;
    private UserInfo requester;
    private UserInfo targetUser;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;

    @Getter
    @Builder
    public static class PredictionInfo {
        private Long id;
        private String title;
        private LocalDate releaseDate;
    }

    @Getter
    @Builder
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String email;
    }

    public static ClaimResponse from(RecipientClaim claim) {
        return ClaimResponse.builder()
                .id(claim.getId())
                .prediction(PredictionInfo.builder()
                        .id(claim.getPrediction().getId())
                        .title(claim.getPrediction().getTitle())
                        .releaseDate(claim.getPrediction().getReleaseDate())
                        .build())
                .requester(UserInfo.builder()
                        .id(claim.getRequester().getId())
                        .nickname(claim.getRequester().getNickname())
                        .build())
                .targetUser(UserInfo.builder()
                        .id(claim.getTargetUser().getId())
                        .nickname(claim.getTargetUser().getNickname())
                        .email(claim.getTargetUser().getEmail())
                        .build())
                .status(claim.getStatus().name())
                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .build();
    }

    public static ClaimResponse withMessage(RecipientClaim claim, String message) {
        ClaimResponse response = from(claim);
        return ClaimResponse.builder()
                .id(response.getId())
                .prediction(response.getPrediction())
                .requester(response.getRequester())
                .targetUser(response.getTargetUser())
                .status(response.getStatus())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .message(message)
                .build();
    }
}
