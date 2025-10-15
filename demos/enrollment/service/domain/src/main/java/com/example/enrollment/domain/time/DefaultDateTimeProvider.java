package com.example.enrollment.domain.time;

import java.time.Instant;

public class DefaultDateTimeProvider implements DateTimeProvider {
    @Override
    public Instant nowUtc() {
        return Instant.now();
    }
}
