package com.example.demo.repository;

import com.example.demo.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    @Query("SELECT ep FROM EventParticipant ep JOIN FETCH ep.user WHERE ep.event.id = :eventId")
    List<EventParticipant> findAllByEventIdWithUser(@Param("eventId") Long eventId);

    long countByEventId(Long eventId);
}