package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateApplicationRequest(
        @NotNull Long userId,
        @NotBlank String message,
        @NotBlank @Size(max = 64) String contact
) {}