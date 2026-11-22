package com.example.demo.repository;

import com.example.demo.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("SELECT a FROM Application a JOIN FETCH a.user WHERE a.event.id = :eventId ORDER BY a.createdAt DESC")
    List<Application> findAllByEventIdWithUser(@Param("eventId") Long eventId);

    Optional<Application> findByEventIdAndUserId(Long eventId, Long userId);
}