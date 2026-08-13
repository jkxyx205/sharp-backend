package com.rick.backend.module.auth.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rick.db.repository.Column;
import com.rick.db.repository.Table;
import com.rick.db.repository.model.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Table(value = "sys_user", comment = "用户表")
public class User extends BaseEntity<Long> {

    @NotBlank
    @Column(nullable = false, comment = "用户名")
    private String username;

    @NotBlank
    @Column(nullable = false, updatable = false, comment = "手机号")
    @Pattern(regexp = "^1(3[0-9]|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$", message = "手机号11位数字")
    String mobile;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, columnDefinition = "varchar(128)", comment = "密码")
    private String password;
//
//    @Column(value = "create_time", updatable = false, comment = "创建时间")
//    private LocalDateTime createTime;
}
