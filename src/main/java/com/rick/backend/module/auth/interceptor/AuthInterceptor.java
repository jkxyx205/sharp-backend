package com.rick.backend.module.auth.interceptor;

import com.rick.backend.module.auth.entity.User;
import com.rick.backend.module.auth.service.AuthService;
import com.rick.backend.module.auth.service.UserContextHolder;
import com.rick.common.http.HttpServletRequestUtils;
import com.rick.common.http.HttpServletResponseUtils;
import com.rick.common.http.model.ResultUtils;
import com.rick.common.util.DeviceUtils;
import com.rick.common.util.model.Device;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    static String AUTHORIZATION_HEADER = "Authorization";
    static String BEARER_PREFIX = "Bearer ";
    static String PARAM_TOKEN = "token";

    AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = resolveToken(request);

        if (authService.validateAndRefresh(token)) {
            log(request, UserContextHolder.get());
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        HttpServletResponseUtils.writeJSON(response, ResultUtils.fail(401, "需要认证才能访问 API"));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) {
        UserContextHolder.remove();
    }

    /**
     * 从请求中解析 token：
     * 优先从 Authorization: Bearer <token> header 中提取，
     * 其次从 query parameter token= 中提取（兼容旧客户端）。
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        // 兼容 query parameter 传参
        String paramToken = request.getParameter(PARAM_TOKEN);
        if (StringUtils.isNotBlank(paramToken)) {
            return paramToken;
        }
        return null;
    }

    private void log(HttpServletRequest request, User user) {
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
