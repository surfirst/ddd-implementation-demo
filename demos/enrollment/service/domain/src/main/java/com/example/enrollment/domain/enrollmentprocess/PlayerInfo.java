package com.example.enrollment.domain.enrollmentprocess;

import java.util.Objects;

public final class PlayerInfo {
    private final String email;
    private final String fullName;

    private PlayerInfo(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
    }

    public static PlayerInfo create(String email, String fullName) {
        Objects.requireNonNull(email);
        Objects.requireNonNull(fullName);
        return new PlayerInfo(email, fullName);
    }

    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
}
