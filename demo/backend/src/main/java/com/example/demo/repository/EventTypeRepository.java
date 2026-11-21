package com.example.demo.repository;

import com.example.demo.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EventTypeRepository extends JpaRepository<EventType, Short> {
    Optional<EventType> findByCode(String code);
}