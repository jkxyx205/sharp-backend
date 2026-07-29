package com.rick.backend.module.auth.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rick.backend.module.auth.config.AuthConstants;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class AuthTokenService {

    private final Cache<String, String> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(AuthConstants.TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .maximumSize(AuthConstants.MAXIMUM_SIZE)
            .build();

    private final Cache<String, Set<String>> usernameTokenCache = Caffeine.newBuilder()
            .expireAfterWrite(AuthConstants.TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .maximumSize(AuthConstants.MAXIMUM_SIZE)
            .build();

    public void manageToken(String username, String deviceKey, String token) {
        tokenCache.put(token, username + ":" + deviceKey);
        Set<String> userTokens = usernameTokenCache.getIfPresent(username);
        if (userTokens == null) {
            userTokens = new java.util.HashSet<>();
        }
        userTokens.add(token);
        usernameTokenCache.put(username, userTokens);
    }

    public String findTokenByDevice(String username, String deviceKey) {
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

    public String getIdentity(String token) {
        return tokenCache.getIfPresent(token);
    }

    public void refreshToken(String token, String username, String deviceKey) {
        manageToken(username, deviceKey, token);
    }

    public void revokeToken(String token) {
        if (!StringUtils.hasText(token)) {
            return;
        }
        String identity = tokenCache.getIfPresent(token);
        if (identity == null) {
            return;
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
    }

    public void revokeUserTokens(String username) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        Set<String> userTokens = usernameTokenCache.getIfPresent(username);
        if (userTokens == null || userTokens.isEmpty()) {
            return;
        }
        for (String token : userTokens) {
            tokenCache.invalidate(token);
        }
        usernameTokenCache.invalidate(username);
    }
}
