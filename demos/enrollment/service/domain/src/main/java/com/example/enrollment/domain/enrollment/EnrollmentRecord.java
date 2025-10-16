package com.example.enrollment.domain.enrollment;

import java.time.Instant;
import java.util.Objects;

public final class EnrollmentRecord {
    private final EnrollmentId id;
    private final String name;
    private final Instant createdAt;

    private EnrollmentRecord(EnrollmentId id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static EnrollmentRecord create(String name) {
        Objects.requireNonNull(name, "name");
        return new EnrollmentRecord(EnrollmentId.newId(), name, Instant.now());
    }

    public static EnrollmentRecord rehydrate(EnrollmentId id, String name, Instant createdAt) {
        return new EnrollmentRecord(id, name, createdAt);
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
