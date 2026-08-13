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

        // 需要填写设置 id，否则抛出异常 java.lang.IllegalArgumentException.class
        Assertions.assertThrows(java.lang.IllegalArgumentException.class, () -> {
            baseService.patch(user);
        });

        user.setId(1113884208805187584L);

        // 如果 mobile 允许修改，250 不是合法的身份证号，就会抛出验证异常 ConstraintViolationException
//        user.setMobile("250");
//        Assertions.assertThrows(javax.validation.ConstraintViolationException.class, () -> {
//            baseService.patch(user);
//        });

        // mobile 不允许修改，修改失败
        user.setMobile("19912349098");
        baseService.patch(user);

        //        baseService.updateById("mobile", user.getId(), "19912349098"); // 可以跳过实体的验证 updatable
    }
}
