package com.stockmanagement.stockmanagementsystem.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.stockmanagement.stockmanagementsystem.exception.ResourceNotFoundException;
import com.stockmanagement.stockmanagementsystem.model.User;
import com.stockmanagement.stockmanagementsystem.model.UserForm;
import com.stockmanagement.stockmanagementsystem.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by("username"));
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Transactional
    public User createUser(UserForm form) {
        normalize(form);
        validateDuplicates(form, null);
        if (!StringUtils.hasText(form.getPassword()) || form.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters.");
        }

        User user = new User();
        copyBasicDetails(form, user);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, UserForm form) {
        User user = getUserById(id);
        normalize(form);
        validateDuplicates(form, id);
        copyBasicDetails(form, user);
        return userRepository.save(user);
    }

    @Transactional
    public void toggleStatus(Long id, String currentUsername) {
        User user = getUserById(id);
        if (user.getUsername().equalsIgnoreCase(currentUsername) && Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalArgumentException("You cannot deactivate your own account.");
        }
        user.setActive(!Boolean.TRUE.equals(user.getActive()));
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(Long id, String password) {
        if (!StringUtils.hasText(password) || password.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters.");
        }
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public UserForm toForm(User user) {
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setFullName(user.getFullName());
        form.setEmail(user.getEmail());
        form.setRole(user.getRole());
        form.setActive(user.getActive());
        return form;
    }

    private void validateDuplicates(UserForm form, Long id) {
        boolean duplicateUsername = id == null
                ? userRepository.existsByUsernameIgnoreCase(form.getUsername())
                : userRepository.existsByUsernameIgnoreCaseAndIdNot(form.getUsername(), id);
        if (duplicateUsername) {
            throw new IllegalArgumentException("A user with this username already exists.");
        }
        if (form.getEmail() != null) {
            boolean duplicateEmail = id == null
                    ? userRepository.existsByEmailIgnoreCase(form.getEmail())
                    : userRepository.existsByEmailIgnoreCaseAndIdNot(form.getEmail(), id);
            if (duplicateEmail) {
                throw new IllegalArgumentException("A user with this email already exists.");
            }
        }
    }

    private void copyBasicDetails(UserForm form, User user) {
        user.setUsername(form.getUsername());
        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail());
        user.setRole(form.getRole());
        user.setActive(Boolean.TRUE.equals(form.getActive()));
    }

    private void normalize(UserForm form) {
        form.setUsername(form.getUsername().trim());
        form.setFullName(form.getFullName().trim());
        form.setEmail(StringUtils.hasText(form.getEmail()) ? form.getEmail().trim() : null);
    }
}
