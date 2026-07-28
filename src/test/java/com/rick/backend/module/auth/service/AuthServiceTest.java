package com.rick.backend.module.auth.service;

import com.rick.backend.module.auth.entity.User;
import com.rick.backend.module.auth.util.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginShouldCreateDistinctTokensForDifferentDevices() {
        User user = User.builder()
                .username("admin")
                .password(PasswordUtils.encrypt("123456", "admin"))
                .build();
        when(userService.findByUsername("admin")).thenReturn(Optional.of(user));

        HttpServletRequest firstRequest = mock(HttpServletRequest.class);
        when(firstRequest.getHeader("X-Device-Id")).thenReturn("device-1");

        HttpServletRequest secondRequest = mock(HttpServletRequest.class);
        when(secondRequest.getHeader("X-Device-Id")).thenReturn("device-2");

        String firstToken = authService.login("admin", "123456", firstRequest);
        String secondToken = authService.login("admin", "123456", secondRequest);

        assertNotEquals(firstToken, secondToken);
        assertTrue(authService.validateAndRefresh(firstToken));
        assertTrue(authService.validateAndRefresh(secondToken));
    }

    @Test
    void logoutCurrentTokenShouldNotAffectOtherTokens() {
        User user = User.builder()
                .username("admin")
                .password(PasswordUtils.encrypt("123456", "admin"))
                .build();
        when(userService.findByUsername("admin")).thenReturn(Optional.of(user));

        HttpServletRequest firstRequest = mock(HttpServletRequest.class);
        when(firstRequest.getHeader("X-Device-Id")).thenReturn("device-1");

        HttpServletRequest secondRequest = mock(HttpServletRequest.class);
        when(secondRequest.getHeader("X-Device-Id")).thenReturn("device-2");

        String firstToken = authService.login("admin", "123456", firstRequest);
        String secondToken = authService.login("admin", "123456", secondRequest);

        assertTrue(authService.logout(firstToken));
        assertFalse(authService.validateAndRefresh(firstToken));
        assertTrue(authService.validateAndRefresh(secondToken));
    }
}
