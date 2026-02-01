package com.timecapsule.controller;

import com.timecapsule.dto.request.CreateClaimRequest;
import com.timecapsule.dto.request.UpdateClaimRequest;
import com.timecapsule.dto.response.ClaimResponse;
import com.timecapsule.security.CustomUserDetails;
import com.timecapsule.service.ClaimService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping("/predictions/{predictionId}/claims")
    public ResponseEntity<ClaimResponse> createClaim(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long predictionId,
            @Valid @RequestBody CreateClaimRequest request) {
        ClaimResponse response = claimService.createClaim(userDetails.getId(), predictionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/claims/received")
    public ResponseEntity<Page<ClaimResponse>> getReceivedClaims(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ClaimResponse> response = claimService.getReceivedClaims(userDetails.getId(), pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/claims/{id}")
    public ResponseEntity<ClaimResponse> updateClaim(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateClaimRequest request) {
        ClaimResponse response = claimService.updateClaim(userDetails.getId(), id, request);
        return ResponseEntity.ok(response);
    }
}
