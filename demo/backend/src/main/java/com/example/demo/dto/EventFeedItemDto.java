package com.example.demo.dto;

import java.time.OffsetDateTime;

public record EventFeedItemDto(
        Long id,
        String title,
        String description,
        Short categoryId,
        String categoryName,
        String typeCode,
        String typeName,
        OffsetDateTime startsAt,
        String address,
        Short minAge,
        String genderRestriction,
        Long authorId,
        String authorNickname,
        long membersCount
) {}