package com.rick.backend.module.auth.interceptor;

import com.rick.backend.module.auth.service.AuthService;
import com.rick.common.http.HttpServletResponseUtils;
import com.rick.common.http.model.ResultUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthInterceptor implements HandlerInterceptor {

    public static final String TOKEN_HEADER = "token";

    AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader(TOKEN_HEADER);
        if (authService.validateAndRefresh(token)) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        HttpServletResponseUtils.writeJSON(response, ResultUtils.fail(401, "需要认证才能访问 API"));
        return false;
    }
}
