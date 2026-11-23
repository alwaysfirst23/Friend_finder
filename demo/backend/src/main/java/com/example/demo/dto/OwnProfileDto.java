package com.example.demo.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record OwnProfileDto(
        Long id,
        String nickname,
        String about,
        String contact,
        String gender,
        String email,
        LocalDate birthDate,
        OffsetDateTime createdAt
) {}
