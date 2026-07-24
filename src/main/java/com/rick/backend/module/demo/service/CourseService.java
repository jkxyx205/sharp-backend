package com.rick.backend.module.demo.service;

import com.rick.backend.module.demo.dao.CourseDAO;
import com.rick.backend.module.demo.entity.Course;
import com.rick.db.plugin.BaseServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
//@RequiredArgsConstructor
@Validated
public class CourseService extends BaseServiceImpl<CourseDAO, Course, Long> {

    public CourseService(CourseDAO baseDAO) {
        super(baseDAO);
    }
}