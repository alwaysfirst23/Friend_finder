package com.example.demo.mapper;

import com.example.demo.dto.EventFeedItemDto;
import com.example.demo.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventFeedItemDto toFeedItemDto(Event event, long membersCount) {
        return new EventFeedItemDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory().getId(),
                event.getCategory().getName(),
                event.getType().getCode(),
                event.getType().getName(),
                event.getStartsAt(),
                event.getAddress(),
                event.getMinAge(),
                event.getGenderRestriction().name(),
                event.getAuthor().getId(),
                event.getAuthor().getNickname(),
                membersCount
        );
    }
}