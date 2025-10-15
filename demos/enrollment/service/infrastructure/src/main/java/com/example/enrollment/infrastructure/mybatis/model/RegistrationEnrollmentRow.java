package com.example.enrollment.infrastructure.mybatis.model;

import java.time.Instant;

public class RegistrationEnrollmentRow {
    private String id;
    private String email;
    private String fullName;
    private String otpPassword;
    private Instant otpExpiration;
    private Integer otpLength;
    private Integer otpLifeSpan;
    private Instant createdAt;
    private String cmsId;
    private String displayName;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getOtpPassword() { return otpPassword; }
    public void setOtpPassword(String otpPassword) { this.otpPassword = otpPassword; }

    public Instant getOtpExpiration() { return otpExpiration; }
    public void setOtpExpiration(Instant otpExpiration) { this.otpExpiration = otpExpiration; }

    public Integer getOtpLength() { return otpLength; }
    public void setOtpLength(Integer otpLength) { this.otpLength = otpLength; }

    public Integer getOtpLifeSpan() { return otpLifeSpan; }
    public void setOtpLifeSpan(Integer otpLifeSpan) { this.otpLifeSpan = otpLifeSpan; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getCmsId() { return cmsId; }
    public void setCmsId(String cmsId) { this.cmsId = cmsId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
