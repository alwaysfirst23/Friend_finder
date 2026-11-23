package com.example.demo.service;

import com.example.demo.dto.EventFeedItemDto;
import com.example.demo.dto.OwnProfileDto;
import com.example.demo.dto.PublicProfileDto;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.Gender;
import com.example.demo.mapper.EventMapper;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String NOT_FOUND = "Пользователь не найден";

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    public PublicProfileDto getPublicProfile(Long id) {
        User user = findActive(id);
        return new PublicProfileDto(
                user.getId(),
                user.getNickname(),
                user.getAbout(),
                user.getContact(),
                genderName(user),
                user.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public OwnProfileDto getOwnProfile(Long userId) {
        return toOwnProfile(findActive(userId));
    }

    @Transactional
    public OwnProfileDto updateOwnProfile(Long userId, UpdateProfileRequest req) {
        User user = findActive(userId);

        if (req.nickname() != null) {
            String nickname = req.nickname().trim();
            if (userRepository.existsByNicknameIgnoreCaseAndIdNot(nickname, userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Этот ник уже занят");
            }
            user.setNickname(nickname);
        }
        if (req.about() != null) {
            user.setAbout(blankToNull(req.about()));
        }
        if (req.contact() != null) {
            user.setContact(blankToNull(req.contact()));
        }
        if (req.gender() != null) {
            user.setGender(Gender.valueOf(req.gender()));
        }

        return toOwnProfile(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public Page<EventFeedItemDto> getUserEvents(Long userId, Pageable pageable) {
        findActive(userId);

        Page<Event> page = eventRepository.findAllRelatedToUser(userId, pageable);
        Map<Long, Long> counts = loadParticipantsCounts(page.getContent());
        return page.map(e -> eventMapper.toFeedItemDto(e, counts.getOrDefault(e.getId(), 0L)));
    }

    /** Заблокированные пользователи для API невидимы: отвечаем так же, как на несуществующего. */
    private User findActive(Long id) {
        return userRepository.findById(id)
                .filter(u -> !u.isBlocked())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    private OwnProfileDto toOwnProfile(User user) {
        return new OwnProfileDto(
                user.getId(),
                user.getNickname(),
                user.getAbout(),
                user.getContact(),
                genderName(user),
                user.getEmail(),
                user.getBirthDate(),
                user.getCreatedAt()
        );
    }

    private static String genderName(User user) {
        return user.getGender() == null ? null : user.getGender().name();
    }

    private static String blankToNull(String value) {
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
