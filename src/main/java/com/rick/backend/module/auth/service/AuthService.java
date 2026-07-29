package com.rick.backend.module.auth.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rick.backend.module.auth.config.AuthConstants;
import com.rick.backend.module.auth.entity.User;
import com.rick.backend.module.auth.util.PasswordUtils;
import com.rick.common.http.exception.BizException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthService {

    UserService userService;

    AuthTokenService authTokenService;

    Cache<String, User> userCache = Caffeine.newBuilder()
            .expireAfterWrite(AuthConstants.TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .maximumSize(AuthConstants.MAXIMUM_SIZE)
            .build();

    public String login(String username, String password, HttpServletRequest request) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new BizException(401, "用户名或密码错误"));
        if (!PasswordUtils.matches(password, username, user.getPassword())) {
            throw new BizException(401, "用户名或密码错误");
        }

        UserContextHolder.set(user);
        userCache.put(username, user);

        String deviceKey = resolveDeviceKey(request);
        String existingToken = authTokenService.findTokenByDevice(username, deviceKey);
        if (StringUtils.hasText(existingToken)) {
//            revokeUserTokens(username);
            authTokenService.refreshToken(existingToken, username, deviceKey);
            return existingToken;
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        authTokenService.manageToken(username, deviceKey, token);
        return token;
    }

    private String resolveDeviceKey(HttpServletRequest request) {
        if (request == null) {
            return "default-device";
        }
        String deviceId = request.getHeader("X-Device-Id");
        if (StringUtils.hasText(deviceId)) {
            return deviceId;
        }
        String userAgent = request.getHeader("User-Agent");
        return StringUtils.hasText(userAgent) ? userAgent : "default-device";
    }

    /**
     * 校验 token，合法则刷新有效期为 120 分钟。
     */
    public boolean validateAndRefresh(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String identity = authTokenService.getIdentity(token);
        if (identity == null) {
            return false;
        }
        String username = identity.split(":", 2)[0];
        String deviceKey = identity.contains(":") ? identity.substring(identity.indexOf(':') + 1) : "default-device";
        authTokenService.refreshToken(token, username, deviceKey);

        User user = userCache.getIfPresent(username);
        if (user == null) {
            user = userService.findByUsername(username)
                    .orElseThrow(() -> new BizException(401, "用户名不存在"));
        }
        userCache.put(username, user);
        UserContextHolder.set(user);
        return true;
    }

    public boolean logout(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String identity = authTokenService.getIdentity(token);
        if (identity == null) {
            return false;
        }
        authTokenService.revokeToken(token);

        String username = identity.split(":", 2)[0];
        userCache.invalidate(username);
        return true;
    }

    public void revokeUserTokens(String username) {
        authTokenService.revokeUserTokens(username);
    }
}
