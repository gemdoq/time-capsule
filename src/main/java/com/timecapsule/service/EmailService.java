package com.timecapsule.service;

import com.timecapsule.entity.PredictionRecipient;
import com.timecapsule.repository.PredictionRecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final PredictionRecipientRepository recipientRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${frontend.base-url:http://localhost:5173}")
    private String baseUrl;

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시
    @Transactional
    public void sendScheduledNotifications() {
        log.info("Starting scheduled notification job...");

        List<PredictionRecipient> recipients =
                recipientRepository.findRecipientsToNotify(LocalDate.now());

        for (PredictionRecipient recipient : recipients) {
            if (recipient.getRecipientEmail() != null) {
                String authorName = recipient.getPrediction().getAuthor().getNickname();
                String accessUrl = baseUrl + "/p/" + recipient.getAccessCode();

                String subject = "[TimeCapsule] 누군가 당신에게 남긴 예측이 공개되었습니다";
                String text = String.format("""
                        안녕하세요,

                        %s님이 당신에게 남긴 예측이 공개되었습니다.

                        아래 링크를 클릭하여 확인해보세요:
                        %s

                        감사합니다.
                        TimeCapsule 팀
                        """, authorName, accessUrl);

                sendEmail(recipient.getRecipientEmail(), subject, text);
                recipient.markAsNotified();
            }
        }

        log.info("Scheduled notification job completed. Sent {} notifications.", recipients.size());
    }
}
