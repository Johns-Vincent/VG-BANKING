package com.deloittevg.service;

import com.deloittevg.dummy.BankAccount;
import com.deloittevg.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    User registerOrUpdate(User user);

    List<User> viewAll();

    User searchById(long userId);

    User searchByEmail(String email);

    void deleteUser(long userId);

    boolean isSameMonth(LocalDateTime l1, LocalDateTime l2);

    void updateAccount(BankAccount account1, BankAccount account2);

    User getUser();
}
