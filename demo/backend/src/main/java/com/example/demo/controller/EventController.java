package com.example.demo.controller;

import com.example.demo.dto.CategoryDto;
import com.example.demo.dto.CreateEventRequest;
import com.example.demo.dto.EventFeedItemDto;
import com.example.demo.dto.EventTypeDto;
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
    public EventFeedItemDto getById(@PathVariable Long id) {
        return eventService.getEventById(id);
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
}