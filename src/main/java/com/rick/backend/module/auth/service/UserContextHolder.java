package com.rick.backend.module.auth.service;

import com.rick.backend.module.auth.entity.User;

/**
 * @author Rick
 * @date 2021-04-08 14:37:00
 */
public class UserContextHolder {

    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    public static void set(User user) {
        CURRENT_USER.set(user);
    }

    public static User get() {
        return CURRENT_USER.get();
    }

    public static void remove() {
        CURRENT_USER.remove();
    }

}
