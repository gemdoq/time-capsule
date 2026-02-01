package com.timecapsule.dto.response;

import com.timecapsule.entity.Prediction;
import com.timecapsule.entity.PredictionRecipient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PredictionResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDate releaseDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AuthorInfo author;
    private RecipientResponse recipient;
    private List<AttachmentResponse> attachments;

    @Getter
    @Builder
    public static class AuthorInfo {
        private Long id;
        private String nickname;
    }

    public static PredictionResponse from(Prediction prediction, String baseUrl) {
        PredictionRecipient recipient = prediction.getRecipients().isEmpty()
                ? null : prediction.getRecipients().get(0);

        return PredictionResponse.builder()
                .id(prediction.getId())
                .title(prediction.getTitle())
                .content(prediction.getContent())
                .releaseDate(prediction.getReleaseDate())
                .status(prediction.isReleased() ? "RELEASED" : "PENDING")
                .createdAt(prediction.getCreatedAt())
                .updatedAt(prediction.getUpdatedAt())
                .author(AuthorInfo.builder()
                        .id(prediction.getAuthor().getId())
                        .nickname(prediction.getAuthor().getNickname())
                        .build())
                .recipient(recipient != null ? RecipientResponse.from(recipient, baseUrl) : null)
                .attachments(prediction.getAttachments().stream()
                        .map(AttachmentResponse::from)
                        .toList())
                .build();
    }
}
