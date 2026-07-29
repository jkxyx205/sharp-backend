package com.rick.backend.module.auth.interceptor;

import com.rick.backend.module.auth.entity.User;
import com.rick.backend.module.auth.service.AuthService;
import com.rick.backend.module.auth.service.UserContextHolder;
import com.rick.common.http.HttpServletRequestUtils;
import com.rick.common.http.HttpServletResponseUtils;
import com.rick.common.http.model.ResultUtils;
import com.rick.common.model.Device;
import com.rick.common.util.DeviceUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    static String TOKEN_HEADER = "token";

    AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader(TOKEN_HEADER);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(TOKEN_HEADER);
        }

        if (authService.validateAndRefresh(token)) {
            log(request, UserContextHolder.get());
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        HttpServletResponseUtils.writeJSON(response, ResultUtils.fail(401, "需要认证才能访问 API"));
        return false;
    }

    public void log(HttpServletRequest request, User user) {
        if (!(request.getRequestURI().matches(".*[.](js|css|png|jpeg|jpg)") ||
                request.getRequestURI().equals("/") ||
                request.getRequestURI().endsWith("/error") ||
                request.getRequestURI().endsWith("/forbidden") ||
                request.getRequestURI().endsWith("/password") ||
                request.getRequestURI().endsWith("/users/change-password") ||
                request.getRequestURI().endsWith("/version") ||
                request.getRequestURI().endsWith("/kaptcha") ||
                request.getRequestURI().endsWith("/login") ||
                request.getRequestURI().endsWith("/logs") ||
                request.getRequestURI().endsWith("/logs/api"))) {
            Device device = DeviceUtils.getCurrentDevice(request);
            String params;
            String requestMethod = request.getMethod();
            //如果请求是POST获取body字符串，否则GET的话用request.getQueryString()获取参数值
            if (StringUtils.equalsIgnoreCase(HttpMethod.POST.name(), requestMethod) || StringUtils.equalsIgnoreCase(HttpMethod.PUT.name(), requestMethod)) {
                params = HttpServletRequestUtils.getBodyString(request);
            } else {
                params = ObjectUtils.defaultIfNull(request.getQueryString(), "");
            }

            log.info("VISIT: 用户{}-{}访问地址{}, method={}, ip={}, 设备类型={}, 参数={}", user.getUsername(), user.getUsername(),  request.getRequestURI()
                    , request.getMethod()
                    , HttpServletRequestUtils.getClientIpAddress(request)
                    , device
                    , " params => " + params);

        }
    }
}
