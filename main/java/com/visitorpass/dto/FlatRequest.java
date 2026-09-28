package com.visitorpass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FlatRequest(
        @NotBlank String flatNumber,
        @NotBlank String block,
        @NotNull @Positive Integer floor
) {
}
