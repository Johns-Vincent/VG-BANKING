package com.deloittevg.service;

import com.deloittevg.dummy.BankAccount;
import com.deloittevg.entity.User;
import com.deloittevg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public User registerOrUpdate(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> viewAll() {
        return userRepository.findAll();
    }

    @Override
    public User searchById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User searchByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean isSameMonth(LocalDateTime l1, LocalDateTime l2) {
        return l1.getYear() == l2.getYear() && l1.getMonth() == l2.getMonth();
    }

    @Override
    public void updateAccount(BankAccount account1, BankAccount account2) {
        account1.setSuffix(account2.getSuffix());
        account1.setFirstName(account2.getFirstName());
        account1.setLastName(account2.getLastName());
        account1.setMiddleName(account2.getMiddleName());
        account1.setNickName(account2.getNickName());
    }

}
