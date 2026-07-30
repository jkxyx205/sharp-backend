package com.rick.backend.module.auth.controller;

import com.rick.backend.module.auth.service.AuthService;
import com.rick.common.http.model.Result;
import com.rick.common.http.model.ResultUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String token = authService.login(body.get("username"), body.get("password"), request);
        return ResultUtils.success(Collections.singletonMap("token", token));
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = resolveBearerToken(request);
        boolean success = authService.logout(token);
        if (success) {
            return ResultUtils.success(null);
        }
        return ResultUtils.fail(401, "未登录或 token 无效");
    }

    /**
     * 从 Authorization: Bearer <token> header 中提取 token。
     */
    private String resolveBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
