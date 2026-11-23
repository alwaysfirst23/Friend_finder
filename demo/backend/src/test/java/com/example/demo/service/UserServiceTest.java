package com.example.demo.service;

import com.example.demo.dto.OwnProfileDto;
import com.example.demo.dto.PublicProfileDto;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.Gender;
import com.example.demo.mapper.EventMapper;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock EventRepository eventRepository;
    @Mock EventMapper eventMapper;
    @InjectMocks UserService userService;

    private User user(long id, boolean blocked) {
        User u = new User();
        u.setNickname("old_nick");
        u.setEmail("a@example.com");
        u.setBirthDate(LocalDate.of(2000, 1, 1));
        u.setAbout("about");
        u.setContact("@tg");
        u.setGender(Gender.female);
        u.setBlocked(blocked);
        u.setId(id);
        return u;
    }

    @Test
    void publicProfileShowsContactButNotPrivateFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, false)));

        PublicProfileDto dto = userService.getPublicProfile(1L);

        assertEquals("old_nick", dto.nickname());
        assertEquals("female", dto.gender());
        assertEquals("@tg", dto.contact());
    }

    @Test
    void blockedUserLooksLikeNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, true)));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.getPublicProfile(2L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void updateChangesOnlyAllowedFields() {
        User u = user(3L, false);
        when(userRepository.findById(3L)).thenReturn(Optional.of(u));
        when(userRepository.existsByNicknameIgnoreCaseAndIdNot("new_nick", 3L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        OwnProfileDto dto = userService.updateOwnProfile(3L,
                new UpdateProfileRequest("  new_nick ", "  hi  ", "   ", "male"));

        assertEquals("new_nick", dto.nickname());
        assertEquals("hi", dto.about());
        assertNull(dto.contact());
        assertEquals("male", dto.gender());
        assertEquals("a@example.com", dto.email());
        assertEquals(LocalDate.of(2000, 1, 1), dto.birthDate());
    }

    @Test
    void nullFieldsAreNotChanged() {
        User u = user(4L, false);
        when(userRepository.findById(4L)).thenReturn(Optional.of(u));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        OwnProfileDto dto = userService.updateOwnProfile(4L, new UpdateProfileRequest(null, null, null, null));

        assertEquals("old_nick", dto.nickname());
        assertEquals("about", dto.about());
        assertEquals("@tg", dto.contact());
        assertEquals("female", dto.gender());
    }

    @Test
    void takenNicknameReturnsConflict() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(user(5L, false)));
        when(userRepository.existsByNicknameIgnoreCaseAndIdNot("Taken", 5L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.updateOwnProfile(5L, new UpdateProfileRequest("Taken", null, null, null)));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }
}
