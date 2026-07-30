package com.rick.backend.module.demo;

import com.rick.backend.BaseTest;
import com.rick.backend.module.auth.entity.User;
import com.rick.backend.module.auth.service.UserService;
import com.rick.common.util.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserTest extends BaseTest<UserService, User, Long> {

    public UserTest(@Autowired UserService baseService) {
        super(baseService);
    }

    @Test
    public void testUpdateById() {
        baseService.updateById("username", 1113884208805187584L, "abc");
    }

    @Test
    public void testUpdateById2() {
        baseService.updateById("username", 1113884208805187584L, Maps.of("username", "rick"));
    }

    @Test
    public void testPatch() {
        User user = User.builder().username("rick").build();
        Assertions.assertThrows(java.lang.IllegalArgumentException.class, () -> {
            baseService.patch(user);
        });

        user.setId(1113884208805187584L);
        baseService.patch(user);
    }
}
