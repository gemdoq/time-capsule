package com.timecapsule.dto.response;

import com.timecapsule.entity.Prediction;
import com.timecapsule.entity.PredictionRecipient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PredictionListResponse {

    private Long id;
    private String title;
    private LocalDate releaseDate;
    private String status;
    private String recipientEmail;
    private Boolean viewed;
    private LocalDateTime createdAt;

    public static PredictionListResponse from(Prediction prediction) {
        PredictionRecipient recipient = prediction.getRecipients().isEmpty()
                ? null : prediction.getRecipients().get(0);

        return PredictionListResponse.builder()
                .id(prediction.getId())
                .title(prediction.getTitle())
                .releaseDate(prediction.getReleaseDate())
                .status(prediction.isReleased() ? "RELEASED" : "PENDING")
                .recipientEmail(recipient != null ? recipient.getRecipientEmail() : null)
                .viewed(recipient != null && recipient.getViewedAt() != null)
                .createdAt(prediction.getCreatedAt())
                .build();
    }
}
