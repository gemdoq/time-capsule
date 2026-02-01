package com.timecapsule.repository;

import com.timecapsule.entity.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    Page<Prediction> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT p FROM Prediction p WHERE p.author.id = :authorId AND p.releaseDate > :today")
    Page<Prediction> findPendingByAuthorId(@Param("authorId") Long authorId,
                                            @Param("today") LocalDate today,
                                            Pageable pageable);

    @Query("SELECT p FROM Prediction p WHERE p.author.id = :authorId AND p.releaseDate <= :today")
    Page<Prediction> findReleasedByAuthorId(@Param("authorId") Long authorId,
                                             @Param("today") LocalDate today,
                                             Pageable pageable);

    @Query("SELECT p FROM Prediction p JOIN p.recipients r WHERE r.notificationSent = false AND p.releaseDate <= :today")
    List<Prediction> findPredictionsToNotify(@Param("today") LocalDate today);
}
