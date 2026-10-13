package com.stockmanagement.stockmanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.stockmanagement.stockmanagementsystem.model.User;
import com.stockmanagement.stockmanagementsystem.model.UserForm;
import com.stockmanagement.stockmanagementsystem.model.UserRole;
import com.stockmanagement.stockmanagementsystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void encodesPasswordWhenCreatingUser() {
        UserForm form = form("admin", "plain-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = new UserService(userRepository, new BCryptPasswordEncoder()).createUser(form);

        assertNotEquals("plain-password", saved.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("plain-password", saved.getPassword()));
    }

    @Test
    void keepsEncodedPasswordWhenEditingUser() {
        User existing = user("admin", "$2a$existing", true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        userService.updateUser(1L, form("renamed", null));

        assertEquals("$2a$existing", existing.getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void preventsCurrentAdminFromDeactivatingSelf() {
        User existing = user("admin", "$2a$existing", true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> userService.toggleStatus(1L, "ADMIN"));
        verify(userRepository, never()).save(any());
    }

    private UserForm form(String username, String password) {
        UserForm form = new UserForm();
        form.setUsername(username);
        form.setPassword(password);
        form.setFullName("Test User");
        form.setRole(UserRole.ADMIN);
        form.setActive(true);
        return form;
    }

    private User user(String username, String password, boolean active) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName("Test User");
        user.setRole(UserRole.ADMIN);
        user.setActive(active);
        return user;
    }
}
