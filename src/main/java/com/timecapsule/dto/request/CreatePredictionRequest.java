package com.timecapsule.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatePredictionRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "공개일은 필수입니다.")
    @Future(message = "공개일은 미래 날짜여야 합니다.")
    private LocalDate releaseDate;

    @NotNull(message = "수신자 정보는 필수입니다.")
    @Valid
    private RecipientInfo recipient;

    @Getter
    @Setter
    public static class RecipientInfo {

        @NotNull(message = "수신자 유형은 필수입니다.")
        private RecipientType type;

        private String email;

        public enum RecipientType {
            EMAIL, LINK_ONLY
        }
    }
}
