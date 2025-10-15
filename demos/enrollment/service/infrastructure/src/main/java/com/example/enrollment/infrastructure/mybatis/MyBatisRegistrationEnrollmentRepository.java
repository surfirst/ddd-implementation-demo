package com.example.enrollment.infrastructure.mybatis;

import com.example.enrollment.domain.enrollmentprocess.EnrolledPlayer;
import com.example.enrollment.domain.enrollmentprocess.Enrollment;
import com.example.enrollment.domain.enrollmentprocess.Otp;
import com.example.enrollment.domain.enrollmentprocess.PlayerInfo;
import com.example.enrollment.domain.enrollmentprocess.RegistrationEnrollmentRepository;
import com.example.enrollment.infrastructure.mybatis.model.RegistrationEnrollmentRow;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@Profile("db")
public class MyBatisRegistrationEnrollmentRepository implements RegistrationEnrollmentRepository {

    private final RegistrationEnrollmentMapper mapper;

    public MyBatisRegistrationEnrollmentRepository(RegistrationEnrollmentMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void addEnrollment(Enrollment enrollment) {
        RegistrationEnrollmentRow row = new RegistrationEnrollmentRow();
        row.setId(enrollment.getId());
        row.setEmail(enrollment.getPlayerInfo().getEmail());
        row.setFullName(enrollment.getPlayerInfo().getFullName());
        row.setOtpPassword(enrollment.getOtp().getPassword());
        row.setOtpExpiration(enrollment.getOtp().getExpirationDate());
        row.setOtpLength(enrollment.getOtp().getPasswordLength());
        row.setOtpLifeSpan(enrollment.getOtp().getPasswordLifeSpan());
        row.setCreatedAt(Instant.now());
        mapper.insert(row);
    }

    @Override
    public Enrollment getEnrollment(String id) {
        RegistrationEnrollmentRow row = mapper.selectById(id);
        if (row == null) return null;
        PlayerInfo pi = PlayerInfo.create(row.getEmail(), row.getFullName());
        Otp otp = Otp.rehydrate(row.getOtpPassword(), row.getOtpExpiration(),
                row.getOtpLength() == null ? Otp.PASSWORD_LENGTH : row.getOtpLength(),
                row.getOtpLifeSpan() == null ? Otp.PASSWORD_LIFE_SPAN : row.getOtpLifeSpan());
        EnrolledPlayer ep = null;
        if (row.getCmsId() != null) {
            ep = new EnrolledPlayer(row.getCmsId(), row.getDisplayName());
        }
        return Enrollment.rehydrate(row.getId(), pi, otp, ep);
    }

    @Override
    public void addEnrolledPlayer(String id, EnrolledPlayer enrolledPlayer) {
        mapper.updateEnrolledPlayer(id, enrolledPlayer.getCmsId(), enrolledPlayer.getDisplayName());
    }
}
