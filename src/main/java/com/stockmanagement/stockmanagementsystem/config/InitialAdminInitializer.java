package com.stockmanagement.stockmanagementsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.stockmanagement.stockmanagementsystem.model.UserForm;
import com.stockmanagement.stockmanagementsystem.model.UserRole;
import com.stockmanagement.stockmanagementsystem.repository.UserRepository;
import com.stockmanagement.stockmanagementsystem.service.UserService;

@Component
public class InitialAdminInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialAdminInitializer.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final Environment environment;

    public InitialAdminInitializer(UserRepository userRepository, UserService userService, Environment environment) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            return;
        }

        String username = environment.getProperty("ADMIN_USERNAME");
        String password = environment.getProperty("ADMIN_PASSWORD");
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            LOGGER.warn("No users exist. Set ADMIN_USERNAME and ADMIN_PASSWORD to create the initial administrator.");
            return;
        }
        if (password.length() < 8) {
            LOGGER.warn("Initial administrator was not created because ADMIN_PASSWORD is shorter than 8 characters.");
            return;
        }

        UserForm form = new UserForm();
        form.setUsername(username);
        form.setPassword(password);
        form.setFullName(environment.getProperty("ADMIN_FULL_NAME", username));
        form.setEmail(environment.getProperty("ADMIN_EMAIL"));
        form.setRole(UserRole.ADMIN);
        form.setActive(true);
        userService.createUser(form);
        LOGGER.info("Initial administrator account created.");
    }
}
