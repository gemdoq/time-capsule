package com.timecapsule.controller;

import com.timecapsule.dto.response.PublicPredictionResponse;
import com.timecapsule.entity.Attachment;
import com.timecapsule.service.AttachmentService;
import com.timecapsule.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final PredictionService predictionService;
    private final AttachmentService attachmentService;

    @GetMapping("/predictions/{accessCode}")
    public ResponseEntity<PublicPredictionResponse> getPrediction(@PathVariable String accessCode) {
        PublicPredictionResponse response = predictionService.getPublicPrediction(accessCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/attachments/{accessCode}/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable String accessCode,
            @PathVariable Long attachmentId) {
        Resource resource = attachmentService.downloadPublicFile(accessCode, attachmentId);
        Attachment attachment = attachmentService.getAttachment(attachmentId);

        String encodedFilename = URLEncoder.encode(attachment.getOriginalName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }
}
