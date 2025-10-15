package com.example.enrollment.domain.enrollment;

import java.time.Instant;
import java.util.Objects;

public final class Enrollment {
    private final EnrollmentId id;
    private final String name;
    private final Instant createdAt;

    private Enrollment(EnrollmentId id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static Enrollment create(String name) {
        Objects.requireNonNull(name, "name");
        return new Enrollment(EnrollmentId.newId(), name, Instant.now());
    }

    public static Enrollment rehydrate(EnrollmentId id, String name, Instant createdAt) {
        return new Enrollment(id, name, createdAt);
    }

    public EnrollmentId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
