package com.rick.backend.module.auth.controller;

import com.rick.backend.module.auth.entity.User;
import com.rick.backend.module.auth.service.UserService;
import com.rick.backend.module.common.controller.BaseApi;
import com.rick.common.http.model.Result;
import com.rick.common.http.model.ResultUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserApi extends BaseApi<UserService, User, Long> {

    public UserApi(UserService baseService) {
        super(baseService);
    }

    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        baseService.changePassword(username, oldPassword, newPassword);
        return ResultUtils.success();
    }
}
