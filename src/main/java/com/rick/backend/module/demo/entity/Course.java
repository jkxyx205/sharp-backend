package com.rick.backend.module.demo.entity;

import com.rick.db.repository.Table;
import com.rick.db.repository.model.EntityId;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程实体类
 * 对应数据库表: courses
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Table(value = "check_courses", comment = "课程表")
public class Course extends EntityId<Long> {

    /**
     * 课程名称
     */
    private String name;

    /**
     * 授课老师
     */
    private String teacher;

    /**
     * 总课时
     */
    private Integer totalHours;

    /**
     * 是否已结束
     */
    private Boolean isEnded;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
