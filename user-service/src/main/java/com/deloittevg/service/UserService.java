package com.deloittevg.service;

import com.deloittevg.dummy.BankAccount;
import com.deloittevg.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {

    User registerUser(User user);

    User updateUser(User user, long userId);

    List<User> viewAll();

    User searchById(long userId);

    User searchByEmail(String email);

    void deleteUser(long userId);

    boolean isSameMonth(LocalDateTime l1, LocalDateTime l2);

    void updateAccount(BankAccount account1, BankAccount account2);
    String welcomeUser();
    User getUserFromAuth();
    public ResponseEntity<List<BankAccount>> viewAccountsByUser(long userId);
    public ResponseEntity<?> openAccount(long userId,BankAccount bankAccount);
    public ResponseEntity<String> deleteAccount(long userId,String accountNo);
    public ResponseEntity<BankAccount> searchByAccountNo(String accountNo);
    public ResponseEntity<?> updateAccount(BankAccount account,long userId,String accountNo);
}
