package com.visitorpass.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

public record ResidentRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^[0-9]{10}$", message = "Phone must contain exactly 10 digits") String phone,
        @NotBlank @Email String email,
        @NotNull @Positive Long flatId
) {
}
