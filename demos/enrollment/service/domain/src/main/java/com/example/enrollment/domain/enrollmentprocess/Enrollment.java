package com.example.enrollment.domain.enrollmentprocess;

import com.example.enrollment.domain.time.DateTimeProvider;

import java.util.Objects;
import java.util.UUID;

public class Enrollment {
    private final String id;
    private final PlayerInfo playerInfo;
    private final Otp otp;
    private EnrolledPlayer enrolledPlayer;

    private Enrollment(String id, PlayerInfo playerInfo, Otp otp) {
        this.id = id;
        this.playerInfo = playerInfo;
        this.otp = otp;
    }

    public static Enrollment start(PlayerInfo playerInfo, DateTimeProvider dateTimeProvider) {
        Objects.requireNonNull(playerInfo);
        Objects.requireNonNull(dateTimeProvider);
        String id = UUID.randomUUID().toString();
        Otp otp = new Otp(dateTimeProvider);
        return new Enrollment(id, playerInfo, otp);
    }

    public static Enrollment rehydrate(String id, PlayerInfo playerInfo, Otp otp, EnrolledPlayer enrolledPlayer) {
        Enrollment e = new Enrollment(id, playerInfo, otp);
        e.setEnrolledPlayer(enrolledPlayer);
        return e;
    }

    public String getId() { return id; }
    public PlayerInfo getPlayerInfo() { return playerInfo; }
    public Otp getOtp() { return otp; }
    public EnrolledPlayer getEnrolledPlayer() { return enrolledPlayer; }

    public void setEnrolledPlayer(EnrolledPlayer enrolledPlayer) {
        this.enrolledPlayer = enrolledPlayer;
    }
}
