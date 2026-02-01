package com.timecapsule.service;

import com.timecapsule.dto.request.CreatePredictionRequest;
import com.timecapsule.dto.response.*;
import com.timecapsule.entity.Prediction;
import com.timecapsule.entity.PredictionRecipient;
import com.timecapsule.entity.User;
import com.timecapsule.exception.CustomException;
import com.timecapsule.exception.ErrorCode;
import com.timecapsule.repository.PredictionRecipientRepository;
import com.timecapsule.repository.PredictionRepository;
import com.timecapsule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final PredictionRecipientRepository recipientRepository;
    private final UserRepository userRepository;

    @Value("${frontend.base-url:http://localhost:5173}")
    private String baseUrl;

    @Transactional
    public PredictionResponse createPrediction(Long userId, CreatePredictionRequest request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (request.getRecipient().getType() == CreatePredictionRequest.RecipientInfo.RecipientType.EMAIL
                && (request.getRecipient().getEmail() == null || request.getRecipient().getEmail().isBlank())) {
            throw new CustomException(ErrorCode.INVALID_RECIPIENT_EMAIL);
        }

        Prediction prediction = Prediction.builder()
                .author(author)
                .title(request.getTitle())
                .content(request.getContent())
                .releaseDate(request.getReleaseDate())
                .build();

        PredictionRecipient recipient = PredictionRecipient.builder()
                .accessCode(generateAccessCode())
                .recipientEmail(request.getRecipient().getType() ==
                        CreatePredictionRequest.RecipientInfo.RecipientType.EMAIL
                        ? request.getRecipient().getEmail() : null)
                .build();

        prediction.addRecipient(recipient);
        Prediction savedPrediction = predictionRepository.save(prediction);

        return PredictionResponse.from(savedPrediction, baseUrl);
    }

    public Page<PredictionListResponse> getMyPredictions(Long userId, String status, Pageable pageable) {
        Page<Prediction> predictions;
        LocalDate today = LocalDate.now();

        if ("PENDING".equalsIgnoreCase(status)) {
            predictions = predictionRepository.findPendingByAuthorId(userId, today, pageable);
        } else if ("RELEASED".equalsIgnoreCase(status)) {
            predictions = predictionRepository.findReleasedByAuthorId(userId, today, pageable);
        } else {
            predictions = predictionRepository.findByAuthorId(userId, pageable);
        }

        return predictions.map(PredictionListResponse::from);
    }

    public PredictionResponse getPrediction(Long userId, Long predictionId) {
        Prediction prediction = predictionRepository.findById(predictionId)
                .orElseThrow(() -> new CustomException(ErrorCode.PREDICTION_NOT_FOUND));

        if (!prediction.getAuthor().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_PREDICTION_OWNER);
        }

        return PredictionResponse.from(prediction, baseUrl);
    }

    @Transactional
    public void deletePrediction(Long userId, Long predictionId) {
        Prediction prediction = predictionRepository.findById(predictionId)
                .orElseThrow(() -> new CustomException(ErrorCode.PREDICTION_NOT_FOUND));

        if (!prediction.getAuthor().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_PREDICTION_OWNER);
        }

        predictionRepository.delete(prediction);
    }

    public Page<ReceivedPredictionResponse> getReceivedPredictions(Long userId, Pageable pageable) {
        Page<PredictionRecipient> recipients = recipientRepository.findByRecipientUserId(userId, pageable);
        return recipients.map(ReceivedPredictionResponse::from);
    }

    @Transactional
    public PublicPredictionResponse getPublicPrediction(String accessCode) {
        PredictionRecipient recipient = recipientRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PREDICTION_NOT_FOUND));

        Prediction prediction = recipient.getPrediction();

        if (!prediction.isReleased()) {
            return PublicPredictionResponse.pending(prediction);
        }

        recipient.markAsViewed();
        return PublicPredictionResponse.released(prediction, accessCode);
    }

    private String generateAccessCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
