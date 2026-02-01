package com.timecapsule.dto.response;

import com.timecapsule.entity.Prediction;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PublicPredictionResponse {

    private String status;
    private String title;
    private String content;
    private LocalDate releaseDate;
    private LocalDateTime createdAt;
    private AuthorInfo author;
    private List<AttachmentResponse> attachments;
    private String message;
    private String proofMessage;

    @Getter
    @Builder
    public static class AuthorInfo {
        private String nickname;
    }

    public static PublicPredictionResponse pending(Prediction prediction) {
        return PublicPredictionResponse.builder()
                .status("PENDING")
                .releaseDate(prediction.getReleaseDate())
                .author(AuthorInfo.builder()
                        .nickname(prediction.getAuthor().getNickname())
                        .build())
                .message("아직 공개되지 않았습니다.")
                .build();
    }

    public static PublicPredictionResponse released(Prediction prediction, String accessCode) {
        return PublicPredictionResponse.builder()
                .status("RELEASED")
                .title(prediction.getTitle())
                .content(prediction.getContent())
                .releaseDate(prediction.getReleaseDate())
                .createdAt(prediction.getCreatedAt())
                .author(AuthorInfo.builder()
                        .nickname(prediction.getAuthor().getNickname())
                        .build())
                .attachments(prediction.getAttachments().stream()
                        .map(a -> AttachmentResponse.forPublic(a, accessCode))
                        .toList())
                .proofMessage(String.format("이 예측은 %d년 %d월 %d일에 작성되었음이 증명됩니다.",
                        prediction.getCreatedAt().getYear(),
                        prediction.getCreatedAt().getMonthValue(),
                        prediction.getCreatedAt().getDayOfMonth()))
                .build();
    }
}
