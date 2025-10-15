package com.example.enrollment.domain.enrollmentprocess;

import com.example.enrollment.domain.time.DateTimeProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OtpTest {

    @Test
    void passwordLengthTest() {
        Otp otp = new Otp();
        assertEquals(Otp.PASSWORD_LENGTH, otp.getPasswordLength());
        assertEquals(Otp.PASSWORD_LENGTH, otp.getPassword().length());
    }

    @Test
    void passwordLengthCanBeChanged() {
        int l = 10;
        Otp otp = new Otp(null, l);
        assertEquals(l, otp.getPasswordLength());
        assertEquals(l, otp.getPassword().length());
    }

    @Test
    void passwordShouldExpire() {
        DateTimeProvider provider = Mockito.mock(DateTimeProvider.class);
        Instant now = Instant.now();
        Mockito.when(provider.nowUtc()).thenReturn(now);

        int span = Otp.PASSWORD_LIFE_SPAN;
        Otp otp = new Otp(provider);

        int span2 = 10;
        Otp otp2 = new Otp(provider, Otp.PASSWORD_LENGTH, span2);

        assertEquals(span, otp.getPasswordLifeSpan());
        assertEquals(now.plusSeconds(span * 60L), otp.getExpirationDate());

        assertEquals(span2, otp2.getPasswordLifeSpan());
        assertEquals(now.plusSeconds(span2 * 60L), otp2.getExpirationDate());
    }

    @Test
    void passwordShouldBeRandom() {
        Otp otp1 = new Otp();
        Otp otp2 = new Otp();
        assertNotEquals(otp1.getPassword(), otp2.getPassword());
    }
}
