package com.timecapsule.dto.response;

import com.timecapsule.entity.PredictionRecipient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RecipientResponse {

    private String type;
    private String email;
    private String accessCode;
    private String accessUrl;
    private Boolean notificationSent;
    private LocalDateTime viewedAt;

    public static RecipientResponse from(PredictionRecipient recipient, String baseUrl) {
        String type = recipient.getRecipientEmail() != null ? "EMAIL" : "LINK_ONLY";

        return RecipientResponse.builder()
                .type(type)
                .email(recipient.getRecipientEmail())
                .accessCode(recipient.getAccessCode())
                .accessUrl(baseUrl + "/p/" + recipient.getAccessCode())
                .notificationSent(recipient.getNotificationSent())
                .viewedAt(recipient.getViewedAt())
                .build();
    }
}
