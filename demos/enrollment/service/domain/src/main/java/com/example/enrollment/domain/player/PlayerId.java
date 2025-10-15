package com.example.enrollment.domain.player;

import java.util.Objects;

public final class PlayerId {
    public enum IdType { PassportNumber, NationalId }

    private final IdType type;
    private final String value;

    private PlayerId(IdType type, String value) {
        this.type = type;
        this.value = value;
    }

    public static PlayerId create(IdType type, String value) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(value);
        return new PlayerId(type, value);
    }

    public IdType getType() { return type; }
    public String getValue() { return value; }
}
