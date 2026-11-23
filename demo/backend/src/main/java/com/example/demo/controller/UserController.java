package com.example.demo.controller;

import com.example.demo.dto.EventFeedItemDto;
import com.example.demo.dto.OwnProfileDto;
import com.example.demo.dto.PublicProfileDto;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * ВРЕМЕННО: идентификация по заголовку X-User-Id, его может подставить любой клиент.
     * Заменить на проверку JWT до выкатки в прод.
     */
    private static final String USER_HEADER = "X-User-Id";

    private final UserService userService;

    @GetMapping("/{id}")
    public PublicProfileDto getPublicProfile(@PathVariable Long id) {
        return userService.getPublicProfile(id);
    }

    @GetMapping("/{id}/events")
    public Page<EventFeedItemDto> getUserEvents(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "startsAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return userService.getUserEvents(id, pageable);
    }

    @GetMapping("/me")
    public OwnProfileDto getMe(@RequestHeader(USER_HEADER) Long userId) {
        return userService.getOwnProfile(userId);
    }

    @PatchMapping("/me")
    public OwnProfileDto updateMe(
            @RequestHeader(USER_HEADER) Long userId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateOwnProfile(userId, request);
    }
}
