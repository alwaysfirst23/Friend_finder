package com.example.demo.dto;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

public record CreateEventRequest(
        @NotNull Long authorId,
        @NotNull Short categoryId,
        @NotBlank String typeCode,
        @NotBlank @Size(max = 150) String title,
        @NotBlank String description,
        @NotNull @Future OffsetDateTime startsAt,
        @NotBlank @Size(max = 255) String address,
        @Min(0) @Max(100) Short minAge,
        String genderRestriction
) {}