package com.example.enrollment.infrastructure.mybatis;

import com.example.enrollment.infrastructure.mybatis.model.EnrollmentRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnrollmentMapper {
    void insert(EnrollmentRow row);
    EnrollmentRow selectById(@Param("id") String id);
    List<EnrollmentRow> selectAll();
}
