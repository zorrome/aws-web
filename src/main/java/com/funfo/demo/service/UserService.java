package com.funfo.demo.service;

import com.funfo.demo.entity.User;

public interface UserService {
    User getUserById(Integer id);

    User addUser(User user);

    User updateUser(User user);

    User getUserOnlyFromMysql(Integer id);

    Boolean deleteUser(Integer id);

    void clearUserCache(Integer id);
}
