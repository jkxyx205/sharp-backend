package com.rick.backend.module.auth.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rick.backend.module.auth.entity.User;
import com.rick.backend.module.auth.util.PasswordUtils;
import com.rick.common.http.exception.BizException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthService {

    private static final long TOKEN_EXPIRE_MINUTES = 120;

    UserService userService;

    Cache<String, String> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    Cache<String, Set<String>> usernameTokenCache = Caffeine.newBuilder()
            .expireAfterWrite(TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    public String login(String username, String password, HttpServletRequest request) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new BizException(401, "用户名或密码错误"));
        if (!PasswordUtils.matches(password, username, user.getPassword())) {
            throw new BizException(401, "用户名或密码错误");
        }

        String deviceKey = resolveDeviceKey(request);
        String existingToken = findTokenByDevice(username, deviceKey);
        if (StringUtils.hasText(existingToken)) {
            manageToken(username, deviceKey, existingToken);
            return existingToken;
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        manageToken(username, deviceKey, token);
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

    private String findTokenByDevice(String username, String deviceKey) {
        Set<String> userTokens = usernameTokenCache.getIfPresent(username);
        if (userTokens == null || userTokens.isEmpty()) {
            return null;
        }
        for (String token : userTokens) {
            String identity = tokenCache.getIfPresent(token);
            if (identity != null && identity.endsWith(":" + deviceKey)) {
                return token;
            }
        }
        return null;
    }

    private void manageToken(String username, String deviceKey, String token) {
        tokenCache.put(token, username + ":" + deviceKey);
        Set<String> userTokens = usernameTokenCache.getIfPresent(username);
        if (userTokens == null) {
            userTokens = new java.util.HashSet<>();
        }
        userTokens.add(token);
        usernameTokenCache.put(username, userTokens);
    }

    /**
     * 校验 token，合法则刷新有效期为 120 分钟。
     */
    public boolean validateAndRefresh(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String identity = tokenCache.getIfPresent(token);
        if (identity == null) {
            return false;
        }
        String username = identity.split(":", 2)[0];
        manageToken(username, identity.contains(":") ? identity.substring(identity.indexOf(':') + 1) : "default-device", token);
        return true;
    }

    public boolean logout(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String identity = tokenCache.getIfPresent(token);
        if (identity == null) {
            return false;
        }
        String username = identity.split(":", 2)[0];

        tokenCache.invalidate(token);
        Set<String> userTokens = usernameTokenCache.getIfPresent(username);
        if (userTokens != null) {
            userTokens.remove(token);
            if (userTokens.isEmpty()) {
                usernameTokenCache.invalidate(username);
            } else {
                usernameTokenCache.put(username, userTokens);
            }
        }
        return true;
    }
}
