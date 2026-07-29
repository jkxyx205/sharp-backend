package com.rick.backend.module.demo.service;

import com.rick.backend.module.demo.dao.CourseDAO;
import com.rick.backend.module.demo.entity.Course;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * 不继承 BaseServiceImpl，手动暴露少量方法
 */
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Validated
public class DemoService {

    CourseDAO courseDAO;

    @Cacheable(value = "courses", key = "#id")
    public Optional<Course> selectById(Long id) {
        return courseDAO.selectById(id);
    }

    @CacheEvict(value = "courses", key = "#id")
    public int deleteById(Long id) {
        return courseDAO.deleteById(id);
    }

    @CachePut(value = "courses", key = "#course.id")
    public Course update(Course course) {
        return courseDAO.update(course);
    }
}