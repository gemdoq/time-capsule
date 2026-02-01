package com.timecapsule.repository;

import com.timecapsule.entity.PredictionRecipient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRecipientRepository extends JpaRepository<PredictionRecipient, Long> {

    Optional<PredictionRecipient> findByAccessCode(String accessCode);

    @Query("SELECT r FROM PredictionRecipient r WHERE r.recipientUser.id = :userId")
    Page<PredictionRecipient> findByRecipientUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM PredictionRecipient r WHERE r.recipientEmail = :email")
    List<PredictionRecipient> findByRecipientEmail(@Param("email") String email);

    @Query("SELECT r FROM PredictionRecipient r WHERE r.notificationSent = false AND r.prediction.releaseDate <= :today AND r.recipientEmail IS NOT NULL")
    List<PredictionRecipient> findRecipientsToNotify(@Param("today") LocalDate today);
}
