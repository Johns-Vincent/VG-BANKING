package com.deloittevg.service;

import com.deloittevg.entity.User;
import com.deloittevg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
