package com.timecapsule.service;

import com.timecapsule.dto.request.CreateClaimRequest;
import com.timecapsule.dto.request.UpdateClaimRequest;
import com.timecapsule.dto.response.ClaimResponse;
import com.timecapsule.entity.Prediction;
import com.timecapsule.entity.PredictionRecipient;
import com.timecapsule.entity.RecipientClaim;
import com.timecapsule.entity.User;
import com.timecapsule.exception.CustomException;
import com.timecapsule.exception.ErrorCode;
import com.timecapsule.repository.PredictionRecipientRepository;
import com.timecapsule.repository.PredictionRepository;
import com.timecapsule.repository.RecipientClaimRepository;
import com.timecapsule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClaimService {

    private final RecipientClaimRepository claimRepository;
    private final PredictionRepository predictionRepository;
    private final PredictionRecipientRepository recipientRepository;
    private final UserRepository userRepository;

    @Transactional
    public ClaimResponse createClaim(Long userId, Long predictionId, CreateClaimRequest request) {
        Prediction prediction = predictionRepository.findById(predictionId)
                .orElseThrow(() -> new CustomException(ErrorCode.PREDICTION_NOT_FOUND));

        if (!prediction.getAuthor().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_PREDICTION_OWNER);
        }

        User targetUser = userRepository.findByEmail(request.getTargetUserEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND,
                        "해당 이메일의 사용자를 찾을 수 없습니다."));

        if (claimRepository.existsByPredictionIdAndTargetUserId(predictionId, targetUser.getId())) {
            throw new CustomException(ErrorCode.CLAIM_ALREADY_EXISTS);
        }

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        RecipientClaim claim = RecipientClaim.builder()
                .prediction(prediction)
                .requester(requester)
                .targetUser(targetUser)
                .build();

        RecipientClaim savedClaim = claimRepository.save(claim);
        return ClaimResponse.from(savedClaim);
    }

    public Page<ClaimResponse> getReceivedClaims(Long userId, Pageable pageable) {
        Page<RecipientClaim> claims = claimRepository.findByTargetUserIdAndStatus(
                userId, RecipientClaim.Status.PENDING, pageable);
        return claims.map(ClaimResponse::from);
    }

    @Transactional
    public ClaimResponse updateClaim(Long userId, Long claimId, UpdateClaimRequest request) {
        RecipientClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLAIM_NOT_FOUND));

        if (!claim.getTargetUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_CLAIM_TARGET);
        }

        if (request.getStatus() == RecipientClaim.Status.PENDING) {
            throw new CustomException(ErrorCode.INVALID_CLAIM_STATUS);
        }

        if (request.getStatus() == RecipientClaim.Status.ACCEPTED) {
            claim.accept();
            linkRecipientToUser(claim);
            return ClaimResponse.withMessage(claim, "연결 요청이 수락되었습니다.");
        } else {
            claim.reject();
            return ClaimResponse.withMessage(claim, "연결 요청이 거절되었습니다.");
        }
    }

    private void linkRecipientToUser(RecipientClaim claim) {
        Prediction prediction = claim.getPrediction();
        User targetUser = claim.getTargetUser();

        for (PredictionRecipient recipient : prediction.getRecipients()) {
            if (recipient.getRecipientUser() == null) {
                recipient.setRecipientUser(targetUser);
                break;
            }
        }
    }
}
