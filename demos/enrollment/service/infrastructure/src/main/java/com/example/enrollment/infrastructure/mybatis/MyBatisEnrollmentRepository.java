package com.example.enrollment.infrastructure.mybatis;

import com.example.enrollment.domain.enrollment.EnrollmentId;
import com.example.enrollment.domain.enrollment.EnrollmentRepository;
import com.example.enrollment.domain.enrollment.EnrollmentRecord;
import com.example.enrollment.infrastructure.mybatis.model.EnrollmentRow;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("db")
public class MyBatisEnrollmentRepository implements EnrollmentRepository {
    private final EnrollmentMapper mapper;

    public MyBatisEnrollmentRepository(EnrollmentMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(EnrollmentRecord enrollment) {
        EnrollmentRow row = new EnrollmentRow();
        row.setId(enrollment.getId().getValue());
        row.setName(enrollment.getName());
        row.setCreatedAt(enrollment.getCreatedAt());
        mapper.insert(row);
    }

    @Override
    public Optional<EnrollmentRecord> findById(EnrollmentId id) {
        EnrollmentRow row = mapper.selectById(id.getValue());
        if (row == null) return Optional.empty();
        return Optional.of(EnrollmentRecord.rehydrate(EnrollmentId.fromString(row.getId()), row.getName(), row.getCreatedAt()));
    }

    @Override
    public List<EnrollmentRecord> findAll() {
        return mapper.selectAll().stream()
                .map(r -> EnrollmentRecord.rehydrate(EnrollmentId.fromString(r.getId()), r.getName(), r.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
