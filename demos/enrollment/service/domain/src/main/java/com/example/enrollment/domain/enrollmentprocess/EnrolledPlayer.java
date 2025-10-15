package com.example.enrollment.domain.enrollmentprocess;

public final class EnrolledPlayer {
    private final String cmsId;
    private final String displayName;

    public EnrolledPlayer(String cmsId, String displayName) {
        this.cmsId = cmsId;
        this.displayName = displayName;
    }

    public String getCmsId() { return cmsId; }
    public String getDisplayName() { return displayName; }
}
