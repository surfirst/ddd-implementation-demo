package com.example.enrollment.infrastructure.mybatis.model;

import java.time.Instant;

public class EnrollmentRow {
    private String id;
    private String name;
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
