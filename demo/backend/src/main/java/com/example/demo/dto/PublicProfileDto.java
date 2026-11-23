package com.example.demo.dto;

import java.time.OffsetDateTime;

/**
 * Публичный профиль пользователя. Намеренно без email, даты рождения и хэша пароля.
 * Контакт отдаётся, как и в карточке мероприятия (authorContact, ParticipantDto).
 */
public record PublicProfileDto(
        Long id,
        String nickname,
        String about,
        String contact,
        String gender,
        OffsetDateTime createdAt
) {}
