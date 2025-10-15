package com.example.enrollment.domain.enrollmentprocess;

import com.example.enrollment.domain.time.DateTimeProvider;

import java.security.SecureRandom;
import java.time.Instant;

public final class Otp {
    public static final int PASSWORD_LENGTH = 6;
    public static final int PASSWORD_LIFE_SPAN = 5;

    private final String password;
    private final int passwordLength;
    private final int passwordLifeSpanMinutes;
    private final Instant expirationDate;

    private Otp(String password, int passwordLength, int passwordLifeSpanMinutes, Instant expirationDate) {
        this.password = password;
        this.passwordLength = passwordLength;
        this.passwordLifeSpanMinutes = passwordLifeSpanMinutes;
        this.expirationDate = expirationDate;
    }

    public Otp() {
        this(null, PASSWORD_LENGTH, PASSWORD_LIFE_SPAN);
    }

    public Otp(DateTimeProvider provider) {
        this(provider, PASSWORD_LENGTH, PASSWORD_LIFE_SPAN);
    }

    public Otp(DateTimeProvider provider, int passwordLength) {
        this(provider, passwordLength, PASSWORD_LIFE_SPAN);
    }

    public Otp(DateTimeProvider provider, int passwordLength, int lifeSpanMinutes) {
        this.passwordLength = passwordLength;
        this.passwordLifeSpanMinutes = lifeSpanMinutes;
        this.password = randomNumeric(passwordLength);
        Instant now = (provider == null ? Instant.now() : provider.nowUtc());
        this.expirationDate = now.plusSeconds(lifeSpanMinutes * 60L);
    }

    public String getPassword() { return password; }
    public int getPasswordLength() { return passwordLength; }
    public int getPasswordLifeSpan() { return passwordLifeSpanMinutes; }
    public Instant getExpirationDate() { return expirationDate; }

    private static String randomNumeric(int length) {
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }

    public static Otp rehydrate(String password, Instant expirationDate, int passwordLength, int lifeSpanMinutes) {
        return new Otp(password, passwordLength, lifeSpanMinutes, expirationDate);
    }
}
