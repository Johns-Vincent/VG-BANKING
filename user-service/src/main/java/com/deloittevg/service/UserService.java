package com.deloittevg.service;

import com.deloittevg.entity.User;

import java.util.List;

public interface UserService {

    User registerOrUpdate(User user);

    List<User> viewAll();

    User searchById(long userId);

    User searchByEmail(String email);

    void deleteUser(long userId);
}
