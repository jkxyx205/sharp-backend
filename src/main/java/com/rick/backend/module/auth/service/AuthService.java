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

    public String login(String username, String password) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new BizException(401, "用户名或密码错误"));
        if (!PasswordUtils.matches(password, username, user.getPassword())) {
            throw new BizException(401, "用户名或密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenCache.put(token, username);
        return token;
    }

    /**
     * 校验 token，合法则刷新有效期为 120 分钟。
     */
    public boolean validateAndRefresh(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String username = tokenCache.getIfPresent(token);
        if (username == null) {
            return false;
        }
        tokenCache.put(token, username);
        return true;
    }
}
