package com.visitorpass.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record PreApprovalRequest(
        @NotNull @Positive Long residentId,
        @NotBlank String visitorName,
        @NotBlank @Pattern(regexp = "^[0-9]{10}$", message = "Visitor phone must contain exactly 10 digits") String visitorPhone,
        @NotNull @Future LocalDateTime visitDateTime,
        @NotNull @Future LocalDateTime expectedExitTime,
        @NotBlank String purpose
) {
}
