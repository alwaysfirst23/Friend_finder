package com.example.demo.dto;

import java.time.OffsetDateTime;

public record ApplicationDto(
        Long id,
        Long eventId,
        Long userId,
        String userNickname,
        String message,
        String contact,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime decidedAt
) {}