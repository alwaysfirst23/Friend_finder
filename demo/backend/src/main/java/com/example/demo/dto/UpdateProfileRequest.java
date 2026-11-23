package com.example.demo.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Редактируемые поля профиля. Поле null — не менять; пустая строка в about/contact — очистить.
 * Дату рождения, email и роль изменить нельзя: в запрос эти поля просто не входят.
 */
public record UpdateProfileRequest(
        @Size(min = 1, max = 50)
        @Pattern(regexp = "^[\\p{L}\\p{N}_.-]+$", message = "Ник: только буквы, цифры, _ . -")
        String nickname,

        @Size(max = 2000)
        String about,

        @Size(max = 64)
        String contact,

        @Pattern(regexp = "^(male|female)$", message = "Пол: male или female")
        String gender
) {}
