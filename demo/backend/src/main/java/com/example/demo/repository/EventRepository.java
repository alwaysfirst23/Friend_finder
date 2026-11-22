package com.example.demo.repository;

import com.example.demo.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query(value = """
            SELECT event_id, COUNT(*) AS cnt
            FROM event_participants
            WHERE event_id IN (:eventIds)
            GROUP BY event_id
            """, nativeQuery = true)
    List<Object[]> countParticipantsByEventIds(@Param("eventIds") Collection<Long> eventIds);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM event_favorites WHERE user_id = :userId AND event_id = :eventId)", nativeQuery = true)
    boolean isFavorite(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = "INSERT INTO event_favorites (user_id, event_id) VALUES (:userId, :eventId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void addFavorite(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = "DELETE FROM event_favorites WHERE user_id = :userId AND event_id = :eventId", nativeQuery = true)
    void removeFavorite(@Param("userId") Long userId, @Param("eventId") Long eventId);
}