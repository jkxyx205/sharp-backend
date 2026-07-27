package com.rick.backend.module.demo;

import com.rick.backend.BaseTest;
import com.rick.backend.module.common.exception.ResourceNotFoundException;
import com.rick.backend.module.demo.entity.Course;
import com.rick.backend.module.demo.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class CourseTest extends BaseTest<CourseService, Course, Long> {

    public CourseTest(@Autowired CourseService baseService) {
        super(baseService);
    }

    @Test
    @Order(1)
    public void testSelectByIdWithNullId() {
//        assertThrows(javax.validation.ConstraintViolationException.class, () -> {
//            baseService.selectById(null);
//        });

        assertThrows(java.lang.IllegalArgumentException.class, () -> {
            // 注解 @Cacheable 使用缓存，id 为 null 则抛出 IllegalArgumentException
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

    /**
     * 编程式缓存
     */
    @Test
    @Order(4)
    public void testSelectById2() {
        for (int i = 0; i < 10; i++) {
            // i = 0，查询数据库，其他从缓存里获取
            baseService.selectById2(1L);
        }
    }

}
