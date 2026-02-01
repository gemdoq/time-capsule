package com.timecapsule.controller;

import com.timecapsule.dto.response.AttachmentResponse;
import com.timecapsule.entity.Attachment;
import com.timecapsule.security.CustomUserDetails;
import com.timecapsule.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/predictions/{predictionId}/attachments")
    public ResponseEntity<AttachmentResponse> uploadFile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long predictionId,
            @RequestParam("file") MultipartFile file) {
        AttachmentResponse response = attachmentService.uploadFile(
                userDetails.getId(), predictionId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<Resource> downloadFile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        Resource resource = attachmentService.downloadFile(userDetails.getId(), id);
        Attachment attachment = attachmentService.getAttachment(id);

        String encodedFilename = URLEncoder.encode(attachment.getOriginalName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    @DeleteMapping("/attachments/{id}")
    public ResponseEntity<Void> deleteFile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        attachmentService.deleteFile(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
