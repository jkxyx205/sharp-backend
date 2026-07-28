package com.rick.backend.module.auth.controller;

import com.rick.backend.module.auth.entity.User;
import com.rick.backend.module.auth.service.UserService;
import com.rick.backend.module.common.controller.BaseApi;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserApi extends BaseApi<UserService, User, Long> {

    public UserApi(UserService baseService) {
        super(baseService);
    }
}
