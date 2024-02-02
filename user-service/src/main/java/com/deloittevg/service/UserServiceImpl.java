package com.deloittevg.service;

import com.deloittevg.client.BankingFeign;
import com.deloittevg.dummy.BankAccount;
import com.deloittevg.entity.User;
import com.deloittevg.repository.UserRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    BankingFeign bankingFeign;


    @Override
    public User registerUser(User user) {
        if(user == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        if(user.getEmail() == null || user.getPassword() == null|| user.getFirstName() == null){
            throw new IllegalArgumentException("Incomplete data");
        }
        else{
            try{
                user.setRole("USER");
                return userRepository.save(user);
            }
            catch (DataIntegrityViolationException ex){
                throw new IllegalArgumentException("Duplicate Email ID found, Check again");
            }
        }
    }

    @Override
    public User updateUser(User user, long userId) {
        User user1 = searchById(userId);
        if(user1 != null){
            user.setRole(user1.getRole());
            user.setUserId(user1.getUserId());
            return userRepository.save(user);
        }
        else{
            return null;
        }
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

    public void updateAccountDetails(BankAccount account1, BankAccount account2) {
        account1.setSuffix(account2.getSuffix());
        account1.setFirstName(account2.getFirstName());
        account1.setLastName(account2.getLastName());
        account1.setMiddleName(account2.getMiddleName());
        account1.setLastModifiedDate(LocalDateTime.now());
        bankingFeign.updateAccount(account1,account1.getAccountNo());
    }

    public void updateNickName(String nickName, BankAccount bankAccount){
        bankAccount.setNickName(nickName);
    }

    @Override
    public String welcomeUser() {
        User user = getUserFromAuth();
        if(user != null) {
            return "Welcome " + user.getFirstName() + " " + user.getLastName();
        }
        else{
            return "Error: User not found";
        }
    }

    @Override
    public User getUserFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return searchByEmail(authentication.getName());

    }

    @Override
    public List<BankAccount> viewAccountsByUser(long userId) {
        return bankingFeign.viewAccountsByUser(userId);
    }

    @Override
    public ResponseEntity<String> openAccount(long userId,BankAccount bankAccount) {
        bankAccount.setUserId(userId);
        List<BankAccount> accounts = viewAccountsByUser(userId);
        if (accounts == null) {
            BankAccount account1 = bankingFeign.openAccount(bankAccount);
            return new ResponseEntity<>("New Account opened Successfully\nAccount No: "
                    + account1.getAccountNo(), HttpStatus.OK);
        }
        else {
            int checkingCount = 0;
            for (BankAccount account : accounts) {
                if (account.getAccountType().equalsIgnoreCase("checking")) {
                    checkingCount += 1;
                }
            }
            if (accounts.size() >= 3) {
                return new ResponseEntity<>("FAILED TO OPEN ACCOUNT\nOnly 3 accounts permitted per user", HttpStatus.NOT_ACCEPTABLE);
            }
            else if (bankAccount.getAccountType().equalsIgnoreCase("checking") && checkingCount >= 2) {
                return new ResponseEntity<>("FAILED TO OPEN ACCOUNT\nOnly 2 Checking accounts permitted per user", HttpStatus.NOT_ACCEPTABLE);
            }
            else {
                bankingFeign.openAccount(bankAccount);
                return new ResponseEntity<>("New Account opened Successfully\nAccount No: "
                        + bankAccount.getAccountNo(), HttpStatus.OK);
            }
        }
    }

    @Override
    public ResponseEntity<String> deleteAccount(long userId,String accountNo) {
        BankAccount account = searchByAccountNo(accountNo);
        if(account == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found !");
        }
        else{
            long daysBetween = ChronoUnit.DAYS.between(account.getCreatedDate(),LocalDateTime.now());
            if(daysBetween <= 30){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body("ACCOUNT CREATED WITHIN 30 DAYS CANNOT BE DELETED\n"+
                                "Days remaining to delete this account: "+ (30-daysBetween));
            }
            else{
                bankingFeign.deleteAccount(accountNo);
                return ResponseEntity.status(HttpStatus.OK).body("Account deleted successfully");
            }
        }
    }

    @Override
    public BankAccount searchByAccountNo(String accountNo) {
        return bankingFeign.searchByAccountNo(accountNo);
    }


    @Override
    public ResponseEntity<String> updateAccount(BankAccount bankAccount,long userId, String accountNo) {
        BankAccount account = searchByAccountNo(accountNo);
        int maxUpdatesPerMonth = 2;
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
        else {
            LocalDateTime lastUpdatedTime = account.getLastModifiedDate();
            if (lastUpdatedTime != null && isSameMonth(lastUpdatedTime,LocalDateTime.now()) && account.getUpdateCount() >= maxUpdatesPerMonth) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Your name can only be updated twice a month, please try next month");
            }
            else if (lastUpdatedTime != null && !isSameMonth(lastUpdatedTime,LocalDateTime.now())) {
                account.setUpdateCount(1);
                updateAccountDetails(account,bankAccount);
                return new ResponseEntity<>("Owner details updated", HttpStatus.OK);
            }
            else {
                account.setUpdateCount(account.getUpdateCount() + 1);
                updateAccountDetails(account,bankAccount);
                return new ResponseEntity<>("Owner details updated", HttpStatus.OK);
            }
        }
    }

    
    @Override
    public ResponseEntity<String> updateNickName(String nickName, String accountNo) {
        BankAccount bankAccount  = searchByAccountNo(accountNo);
        if(bankAccount != null){
            bankAccount.setNickName(nickName);
            bankingFeign.updateAccount(bankAccount,accountNo);
            return ResponseEntity.status(HttpStatus.OK).body("Nickname updated");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
    }
}
