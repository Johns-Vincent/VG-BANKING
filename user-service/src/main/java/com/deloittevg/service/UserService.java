package com.deloittevg.service;

import com.deloittevg.dummy.BankAccount;
import com.deloittevg.entity.User;
import org.springframework.http.ResponseEntity;

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

    String welcomeUser();
    User getUserFromAuth();
    List<BankAccount> viewAccountsByUser(long userId);
    ResponseEntity<String> openAccount(long userId,BankAccount bankAccount);
    ResponseEntity<String> deleteAccount(long userId,String accountNo);
    BankAccount searchByAccountNo(String accountNo);
    ResponseEntity<String> updateAccount(BankAccount account,long userId,String accountNo);

    ResponseEntity<String> updateNickName(String nickName, String accountNo);


}
