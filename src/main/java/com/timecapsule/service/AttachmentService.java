package com.timecapsule.service;

import com.timecapsule.config.FileConfig;
import com.timecapsule.dto.response.AttachmentResponse;
import com.timecapsule.entity.Attachment;
import com.timecapsule.entity.Prediction;
import com.timecapsule.entity.PredictionRecipient;
import com.timecapsule.exception.CustomException;
import com.timecapsule.exception.ErrorCode;
import com.timecapsule.repository.AttachmentRepository;
import com.timecapsule.repository.PredictionRecipientRepository;
import com.timecapsule.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final PredictionRepository predictionRepository;
    private final PredictionRecipientRepository recipientRepository;
    private final FileConfig fileConfig;

    @Transactional
    public AttachmentResponse uploadFile(Long userId, Long predictionId, MultipartFile file) {
        Prediction prediction = predictionRepository.findById(predictionId)
                .orElseThrow(() -> new CustomException(ErrorCode.PREDICTION_NOT_FOUND));

        if (!prediction.getAuthor().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_PREDICTION_OWNER);
        }

        String originalName = file.getOriginalFilename();
        String storedName = UUID.randomUUID().toString() + getExtension(originalName);
        Path filePath = fileConfig.getUploadPath().resolve(storedName);

        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        Attachment attachment = Attachment.builder()
                .prediction(prediction)
                .originalName(originalName)
                .storedName(storedName)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return AttachmentResponse.from(savedAttachment);
    }

    public Resource downloadFile(Long userId, Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ATTACHMENT_NOT_FOUND));

        if (!attachment.getPrediction().getAuthor().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return loadFileAsResource(attachment);
    }

    public Resource downloadPublicFile(String accessCode, Long attachmentId) {
        PredictionRecipient recipient = recipientRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PREDICTION_NOT_FOUND));

        Prediction prediction = recipient.getPrediction();
        if (!prediction.isReleased()) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ATTACHMENT_NOT_FOUND));

        if (!attachment.getPrediction().getId().equals(prediction.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return loadFileAsResource(attachment);
    }

    @Transactional
    public void deleteFile(Long userId, Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ATTACHMENT_NOT_FOUND));

        if (!attachment.getPrediction().getAuthor().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        try {
            Files.deleteIfExists(Path.of(attachment.getFilePath()));
        } catch (IOException e) {
            // Log but don't fail
        }

        attachmentRepository.delete(attachment);
    }

    public Attachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ATTACHMENT_NOT_FOUND));
    }

    private Resource loadFileAsResource(Attachment attachment) {
        try {
            Path filePath = Path.of(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new CustomException(ErrorCode.ATTACHMENT_NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.ATTACHMENT_NOT_FOUND);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }
}
