package com.example.enrollment.infrastructure.mybatis;

import com.example.enrollment.infrastructure.mybatis.model.RegistrationEnrollmentRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RegistrationEnrollmentMapper {
    void insert(@Param("row") RegistrationEnrollmentRow row);
    RegistrationEnrollmentRow selectById(@Param("id") String id);
    void updateEnrolledPlayer(@Param("id") String id,
                              @Param("cmsId") String cmsId,
                              @Param("displayName") String displayName);
}
