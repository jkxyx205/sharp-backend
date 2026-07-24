package com.rick.backend.module.demo.controller;

import com.rick.backend.module.common.controller.BaseApi;
import com.rick.backend.module.demo.entity.Course;
import com.rick.backend.module.demo.service.CourseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("courses")
public class CourseApi extends BaseApi<CourseService, Course, Long> {

    public CourseApi(CourseService baseService) {
        super(baseService);
    }
}
