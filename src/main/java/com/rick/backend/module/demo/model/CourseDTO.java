package com.rick.backend.module.demo.model;

import com.rick.backend.module.auth.entity.User;
import com.rick.db.repository.Select;
import com.rick.db.repository.Table;
import com.rick.db.repository.model.EntityId;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Table
@Data
public class CourseDTO extends EntityId<Long> {

    private Long userId;

    @Select(
            entityClass = User.class,
            value = "WHERE id = :userId",
            params = "userId@userId", nullWhenParamsIsNull = "userId"
    )
    private User user;

    @Select(
            entityClass = User.class,
            value = "WHERE id = :userId",
            params = "userId@userId", nullWhenParamsIsNull = "userId"
    )
    private Map<String, Object> map;

    @Select(
            value = "SELECT * FROM sys_user"
    )
    private List<Map<String, Object>> userList;

    @Select(
            entityClass = User.class,
            value = "WHERE id = :userId",
            params = "userId@userId", nullWhenParamsIsNull = "userId"
    )
    private List<User> userList2;
}
