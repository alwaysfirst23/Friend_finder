package com.example.demo.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record EventDetailsDto(
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
        String authorContact,
        long membersCount,
        Short maxParticipants,
        boolean isFavorite,
        String myApplicationStatus,
        List<ParticipantDto> participants
) {}