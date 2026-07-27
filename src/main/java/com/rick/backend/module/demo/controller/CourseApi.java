package com.rick.backend.module.demo.controller;

import com.rick.backend.module.common.controller.BaseApi;
import com.rick.backend.module.demo.entity.Course;
import com.rick.backend.module.demo.service.CourseService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("courses")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
//@RequiredArgsConstructor
public class CourseApi extends BaseApi<CourseService, Course, Long> {

    public CourseApi(CourseService baseService) {
        super(baseService);
    }
}
