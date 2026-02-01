package com.timecapsule.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN", "만료된 토큰입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "현재 비밀번호가 올바르지 않습니다."),

    // Prediction
    PREDICTION_NOT_FOUND(HttpStatus.NOT_FOUND, "PREDICTION_NOT_FOUND", "예측을 찾을 수 없습니다."),
    NOT_PREDICTION_OWNER(HttpStatus.FORBIDDEN, "NOT_PREDICTION_OWNER", "해당 예측의 작성자가 아닙니다."),
    INVALID_RECIPIENT_EMAIL(HttpStatus.BAD_REQUEST, "INVALID_RECIPIENT_EMAIL", "수신자 이메일이 필요합니다."),

    // Attachment
    ATTACHMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ATTACHMENT_NOT_FOUND", "첨부파일을 찾을 수 없습니다."),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "FILE_TOO_LARGE", "파일 크기는 10MB를 초과할 수 없습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다."),

    // Claim
    CLAIM_NOT_FOUND(HttpStatus.NOT_FOUND, "CLAIM_NOT_FOUND", "연결 요청을 찾을 수 없습니다."),
    CLAIM_ALREADY_EXISTS(HttpStatus.CONFLICT, "CLAIM_ALREADY_EXISTS", "이미 연결 요청이 존재합니다."),
    NOT_CLAIM_TARGET(HttpStatus.FORBIDDEN, "NOT_CLAIM_TARGET", "해당 연결 요청의 대상이 아닙니다."),
    INVALID_CLAIM_STATUS(HttpStatus.BAD_REQUEST, "INVALID_CLAIM_STATUS", "유효하지 않은 상태입니다."),

    // Common
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
