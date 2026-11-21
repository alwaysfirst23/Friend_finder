package com.example.demo.service;

import com.example.demo.dto.CategoryDto;
import com.example.demo.dto.CreateEventRequest;
import com.example.demo.dto.EventFeedItemDto;
import com.example.demo.dto.EventTypeDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventType;
import com.example.demo.entity.enums.GenderRestriction;
import com.example.demo.entity.User;
import com.example.demo.mapper.EventMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.EventTypeRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventTypeRepository eventTypeRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    public Page<EventFeedItemDto> getEventsFeed(
            Short categoryId,
            String typeCode,
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo,
            String search,
            Pageable pageable
    ) {
        Specification<Event> spec = (root, query, cb) -> {
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("author", JoinType.INNER);
                root.fetch("category", JoinType.INNER);
                root.fetch("type", JoinType.INNER);
            }
            return cb.conjunction();
        };

        if (categoryId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("id"), categoryId));
        }
        if (typeCode != null && !typeCode.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("type").get("code"), typeCode));
        }
        if (dateFrom != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startsAt"), dateFrom));
        }
        if (dateTo != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("startsAt"), dateTo));
        }
        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            ));
        }

        Page<Event> page = eventRepository.findAll(spec, pageable);
        Map<Long, Long> counts = loadParticipantsCounts(page.getContent());

        return page.map(e -> eventMapper.toFeedItemDto(e, counts.getOrDefault(e.getId(), 0L)));
    }

    @Transactional(readOnly = true)
    public EventFeedItemDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Мероприятие не найдено"));
        Map<Long, Long> counts = loadParticipantsCounts(List.of(event));
        return eventMapper.toFeedItemDto(event, counts.getOrDefault(event.getId(), 0L));
    }

    @Transactional
    public EventFeedItemDto createEvent(CreateEventRequest request) {
        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Автор не найден"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Категория не найдена"));
        EventType type = eventTypeRepository.findByCode(request.typeCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Тип мероприятия не найден"));

        Event event = new Event();
        event.setAuthor(author);
        event.setCategory(category);
        event.setType(type);
        event.setTitle(request.title().trim());
        event.setDescription(request.description().trim());
        event.setStartsAt(request.startsAt());
        event.setAddress(request.address().trim());
        event.setMinAge(request.minAge());
        event.setGenderRestriction(request.genderRestriction() == null
                ? GenderRestriction.any
                : GenderRestriction.valueOf(request.genderRestriction()));

        Event saved = eventRepository.save(event);
        return eventMapper.toFeedItemDto(saved, 0L);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventTypeDto> getEventTypes() {
        return eventTypeRepository.findAll().stream()
                .map(t -> new EventTypeDto(t.getId(), t.getCode(), t.getName()))
                .toList();
    }

    private Map<Long, Long> loadParticipantsCounts(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> ids = events.stream().map(Event::getId).toList();
        return eventRepository.countParticipantsByEventIds(ids).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue()
                ));
    }
}