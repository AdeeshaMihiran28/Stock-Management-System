package com.stockmanagement.stockmanagementsystem.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordForm {

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 72, message = "Password must contain between 8 and 72 characters.")
    private String password;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
