package com.stockmanagement.stockmanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.stockmanagement.stockmanagementsystem.model.User;
import com.stockmanagement.stockmanagementsystem.model.UserRole;
import com.stockmanagement.stockmanagementsystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UserRepository userRepository;

    @Test
    void mapsRoleAndInactiveStatusToSpringSecurity() {
        User user = new User();
        user.setUsername("clerk");
        user.setPassword("$2a$encoded");
        user.setFullName("Stock Clerk");
        user.setRole(UserRole.STOCK_CLERK);
        user.setActive(false);
        when(userRepository.findByUsernameIgnoreCase("clerk")).thenReturn(Optional.of(user));

        UserDetails details = new CustomUserDetailsService(userRepository).loadUserByUsername("clerk");

        assertFalse(details.isEnabled());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_STOCK_CLERK")));
    }
}
