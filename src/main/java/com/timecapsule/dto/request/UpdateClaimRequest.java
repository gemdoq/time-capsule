package com.timecapsule.dto.request;

import com.timecapsule.entity.RecipientClaim;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateClaimRequest {

    @NotNull(message = "상태는 필수입니다.")
    private RecipientClaim.Status status;
}
