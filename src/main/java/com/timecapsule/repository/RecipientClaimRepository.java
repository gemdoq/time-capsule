package com.timecapsule.repository;

import com.timecapsule.entity.RecipientClaim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipientClaimRepository extends JpaRepository<RecipientClaim, Long> {

    Page<RecipientClaim> findByTargetUserId(Long targetUserId, Pageable pageable);

    Page<RecipientClaim> findByTargetUserIdAndStatus(Long targetUserId,
                                                      RecipientClaim.Status status,
                                                      Pageable pageable);

    boolean existsByPredictionIdAndTargetUserId(Long predictionId, Long targetUserId);
}
