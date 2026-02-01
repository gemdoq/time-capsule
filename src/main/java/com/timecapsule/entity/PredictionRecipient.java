package com.timecapsule.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_recipients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_id", nullable = false)
    private Prediction prediction;

    @Column(name = "recipient_email", length = 255)
    private String recipientEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser;

    @Column(name = "access_code", nullable = false, unique = true, length = 32)
    private String accessCode;

    @Column(name = "notification_sent", nullable = false)
    @Builder.Default
    private Boolean notificationSent = false;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void markAsViewed() {
        if (this.viewedAt == null) {
            this.viewedAt = LocalDateTime.now();
        }
    }

    public void markAsNotified() {
        this.notificationSent = true;
        this.notifiedAt = LocalDateTime.now();
    }
}
