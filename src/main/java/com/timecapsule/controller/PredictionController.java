package com.timecapsule.controller;

import com.timecapsule.dto.request.CreatePredictionRequest;
import com.timecapsule.dto.response.PredictionListResponse;
import com.timecapsule.dto.response.PredictionResponse;
import com.timecapsule.dto.response.ReceivedPredictionResponse;
import com.timecapsule.security.CustomUserDetails;
import com.timecapsule.service.PredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping
    public ResponseEntity<PredictionResponse> createPrediction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePredictionRequest request) {
        PredictionResponse response = predictionService.createPrediction(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<PredictionListResponse>> getMyPredictions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PredictionListResponse> response = predictionService.getMyPredictions(
                userDetails.getId(), status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PredictionResponse> getPrediction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        PredictionResponse response = predictionService.getPrediction(userDetails.getId(), id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrediction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        predictionService.deletePrediction(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/received")
    public ResponseEntity<Page<ReceivedPredictionResponse>> getReceivedPredictions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReceivedPredictionResponse> response = predictionService.getReceivedPredictions(
                userDetails.getId(), pageable);
        return ResponseEntity.ok(response);
    }
}
