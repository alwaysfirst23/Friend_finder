package com.example.demo.controller;

import com.example.demo.dto.ApplicationDto;
import com.example.demo.dto.CategoryDto;
import com.example.demo.dto.CreateApplicationRequest;
import com.example.demo.dto.CreateEventRequest;
import com.example.demo.dto.EventDetailsDto;
import com.example.demo.dto.EventFeedItemDto;
import com.example.demo.dto.EventTypeDto;
import com.example.demo.entity.enums.ApplicationStatus;
import com.example.demo.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public Page<EventFeedItemDto> getFeed(
            @RequestParam(required = false) Short categoryId,
            @RequestParam(required = false) String typeCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTo,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "startsAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return eventService.getEventsFeed(categoryId, typeCode, dateFrom, dateTo, search, pageable);
    }

    @GetMapping("/{id}")
    public EventDetailsDto getById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId
    ) {
        return eventService.getEventDetails(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFeedItemDto create(@Valid @RequestBody CreateEventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories() {
        return eventService.getCategories();
    }

    @GetMapping("/types")
    public List<EventTypeDto> getEventTypes() {
        return eventService.getEventTypes();
    }

    @PostMapping("/{id}/favorite")
    public Map<String, Boolean> toggleFavorite(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        return Map.of("isFavorite", eventService.toggleFavorite(id, userId));
    }

    @PostMapping("/{id}/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationDto apply(
            @PathVariable Long id,
            @Valid @RequestBody CreateApplicationRequest request
    ) {
        return eventService.applyToEvent(id, request);
    }

    @GetMapping("/{id}/applications")
    public List<ApplicationDto> getApplications(@PathVariable Long id) {
        return eventService.getEventApplications(id);
    }

    @PatchMapping("/{id}/applications/{appId}")
    public ApplicationDto decideApplication(
            @PathVariable Long id,
            @PathVariable Long appId,
            @RequestParam ApplicationStatus status
    ) {
        return eventService.decideApplication(id, appId, status);
    }
}