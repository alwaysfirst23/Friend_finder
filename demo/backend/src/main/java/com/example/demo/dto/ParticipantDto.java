package com.example.demo.dto;

public record ParticipantDto(
        Long userId,
        String nickname,
        String about,
        String contact
) {}