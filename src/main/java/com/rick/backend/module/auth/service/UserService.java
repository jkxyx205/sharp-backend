package com.rick.backend.module.auth.service;

import com.rick.backend.module.auth.dao.UserDAO;
import com.rick.backend.module.auth.entity.User;
import com.rick.backend.module.auth.util.PasswordUtils;
import com.rick.common.http.exception.BizException;
import com.rick.db.plugin.BaseServiceImpl;
import com.rick.db.util.OperatorUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Validated
public class UserService extends BaseServiceImpl<UserDAO, User, Long> {

    AuthTokenService authTokenService;

    public UserService(UserDAO baseDAO, AuthTokenService authTokenService) {
        super(baseDAO);
        this.authTokenService = authTokenService;
    }

    public Optional<User> findByUsername(String username) {
        return OperatorUtils.expectedAsOptional(select(User.builder().username(username).build()));
    }

    public User changePassword(String username, String oldPassword, String newPassword) {
        User user = findByUsername(username)
                .orElseThrow(() -> new BizException(401, "用户不存在"));
        if (!PasswordUtils.matches(oldPassword, username, user.getPassword())) {
            throw new BizException(401, "旧密码不正确");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new BizException(400, "新密码不能为空");
        }
        user.setPassword(newPassword);
        User updatedUser = update(user);
        authTokenService.revokeUserTokens(username);
        return updatedUser;
    }

    @Override
    public User insert(User user) {
        if (!StringUtils.hasText(user.getPassword())) {
            throw new BizException(400, "密码不能为空");
        }
        encryptPassword(user);
//        if (user.getCreateTime() == null) {
//            user.setCreateTime(LocalDateTime.now());
//        }
        return super.insert(user);
    }

    @Override
    public User update(User user) {
        preparePasswordForUpdate(user);
        return super.update(user);
    }

    @Override
    public User insertOrUpdate(User user) {
        if (user.getId() == null) {
            return insert(user);
        }
        return update(user);
    }

    private void encryptPassword(User user) {
        user.setPassword(PasswordUtils.encrypt(user.getPassword(), user.getUsername()));
    }

    private void preparePasswordForUpdate(User user) {
        if (StringUtils.hasText(user.getPassword())) {
            encryptPassword(user);
            return;
        }
        selectById(user.getId()).ifPresent(existing -> user.setPassword(existing.getPassword()));
    }
}
