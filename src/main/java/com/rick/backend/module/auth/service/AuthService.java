package com.rick.backend.module.auth.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rick.common.http.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "123456";
    private static final long TOKEN_EXPIRE_MINUTES = 120;

    private final Cache<String, String> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    public String login(String username, String password) {
        if (!VALID_USERNAME.equals(username) || !VALID_PASSWORD.equals(password)) {
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
