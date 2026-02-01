package com.timecapsule.dto.response;

import com.timecapsule.entity.Attachment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AttachmentResponse {

    private Long id;
    private String originalName;
    private Long fileSize;
    private String mimeType;
    private String downloadUrl;
    private LocalDateTime createdAt;

    public static AttachmentResponse from(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .originalName(attachment.getOriginalName())
                .fileSize(attachment.getFileSize())
                .mimeType(attachment.getMimeType())
                .downloadUrl("/attachments/" + attachment.getId() + "/download")
                .createdAt(attachment.getCreatedAt())
                .build();
    }

    public static AttachmentResponse forPublic(Attachment attachment, String accessCode) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .originalName(attachment.getOriginalName())
                .fileSize(attachment.getFileSize())
                .mimeType(attachment.getMimeType())
                .downloadUrl("/public/attachments/" + accessCode + "/" + attachment.getId())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
