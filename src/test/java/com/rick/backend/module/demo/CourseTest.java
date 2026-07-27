package com.rick.backend.module.demo;

import com.rick.backend.BaseTest;
import com.rick.backend.module.common.exception.ResourceNotFoundException;
import com.rick.backend.module.demo.entity.Course;
import com.rick.backend.module.demo.service.CourseService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourseTest extends BaseTest<CourseService, Course, Long> {

    public CourseTest(@Autowired CourseService baseService) {
        super(baseService);
    }

    @Test
    @Order(1)
    public void testSelectByIdWithNullId() {
        assertThrows(javax.validation.ConstraintViolationException.class, () -> {
            baseService.selectById(null);
        });
    }

    @Test
    @Order(2)
    public void testInsertWithBlankName() {
        assertThrows(javax.validation.ConstraintViolationException.class, () -> {
            super.testInsert(Course.builder().build());
        });
    }

    @Test
    @Order(3)
    public void testInsert2() {
        super.testInsertAndUpdate(Course.builder()
                        .name("Test")
                        .teacher("Rick")
                        .totalHours(10)
                        .isEnded(false)
                .build());
    }

    @Test
    @Order(4)
    public void testSelectByExistId() {
        Course course = baseService.selectById(1L).orElseThrow(() -> new ResourceNotFoundException());
        assertEquals("\uD83C\uDFC0 篮球基础徐教练班", course.getName());
    }
}
