package com.example.enrollment.domain.enrollment;

import java.util.Objects;
import java.util.UUID;

public final class EnrollmentId {
    private final String value;

    private EnrollmentId(String value) {
        this.value = value;
    }

    public static EnrollmentId newId() {
        return new EnrollmentId(UUID.randomUUID().toString());
    }

    public static EnrollmentId fromString(String value) {
        return new EnrollmentId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentId that = (EnrollmentId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
