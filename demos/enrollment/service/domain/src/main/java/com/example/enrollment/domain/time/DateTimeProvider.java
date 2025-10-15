package com.example.enrollment.domain.time;

import java.time.Instant;

public interface DateTimeProvider {
    Instant nowUtc();
}
