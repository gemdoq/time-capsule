package com.timecapsule.dto.response;

import com.timecapsule.entity.Prediction;
import com.timecapsule.entity.PredictionRecipient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReceivedPredictionResponse {

    private Long id;
    private String title;
    private LocalDate releaseDate;
    private String status;
    private AuthorInfo author;
    private LocalDateTime viewedAt;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    public static class AuthorInfo {
        private Long id;
        private String nickname;
    }

    public static ReceivedPredictionResponse from(PredictionRecipient recipient) {
        Prediction prediction = recipient.getPrediction();

        return ReceivedPredictionResponse.builder()
                .id(prediction.getId())
                .title(prediction.isReleased() ? prediction.getTitle() : null)
                .releaseDate(prediction.getReleaseDate())
                .status(prediction.isReleased() ? "RELEASED" : "PENDING")
                .author(AuthorInfo.builder()
                        .id(prediction.getAuthor().getId())
                        .nickname(prediction.getAuthor().getNickname())
                        .build())
                .viewedAt(recipient.getViewedAt())
                .createdAt(prediction.getCreatedAt())
                .build();
    }
}
