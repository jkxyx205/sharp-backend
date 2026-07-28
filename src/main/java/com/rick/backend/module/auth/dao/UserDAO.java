package com.rick.backend.module.auth.dao;

import com.rick.backend.module.auth.entity.User;
import com.rick.db.repository.EntityDAOImpl;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO extends EntityDAOImpl<User, Long> {
}
