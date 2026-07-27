package com.rick.backend.module.demo.controller;

import com.rick.backend.module.demo.entity.Course;
import com.rick.backend.module.demo.service.CourseService;
import com.rick.common.http.exception.BizException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("demos")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class DemoController {

    CourseService courseService;

    @GetMapping
    public ResponseEntity<List<Course>> getCourses() {
        return ResponseEntity.ok(courseService.selectAll());
    }

    @GetMapping("courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable long id) {
        return ResponseEntity.ok(courseService.selectById(id).get());
    }

    @GetMapping("error")
    public String error() {
        throw new BizException("error");
    }
}
