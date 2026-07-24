package com.rick.backend.module.demo.dao;

import com.rick.backend.module.demo.entity.Course;
import com.rick.db.repository.EntityDAOImpl;
import org.springframework.stereotype.Repository;

@Repository
public class CourseDAO extends EntityDAOImpl<Course, Long> {
}
