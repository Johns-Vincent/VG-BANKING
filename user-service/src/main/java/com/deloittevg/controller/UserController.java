package com.deloittevg.controller;

import com.deloittevg.client.BankingFeign;
import com.deloittevg.dummy.BankAccount;
import com.deloittevg.entity.User;
import com.deloittevg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    BankingFeign bankingFeign;

    @GetMapping("/login")
    public String userLogin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.searchByEmail(username);
        if(user != null) {
            return "Welcome " + user.getFirstName() + " " + user.getLastName();
        }
        else{
            return "Error: User not found";
        }
    }

    @GetMapping("/details")
    public RedirectView redirectToConsole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.searchByEmail(username);
        return new RedirectView("/user/" + user.getUserId() + "/details");
    }

    @PostMapping("/register")
    public ResponseEntity<?>registerUser(@RequestBody User user) {
        try {
            user.setRole("USER");
            User user1 = userService.registerOrUpdate(user);
            return ResponseEntity.status(HttpStatus.OK).body("User registered successfully\nUser ID : "
            +user1.getUserId());
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Error:" + ex);
        }

    }
    @PutMapping("/{userId}/update")
    public String updateUser(@RequestBody User user, @PathVariable long userId){
        user.setUserId(userId);
        userService.registerOrUpdate(user);
        return "The user details of "+user.getFirstName()+" "+user.getLastName()+
                " has been updated.";
    }

    @GetMapping("/all")
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> viewAll(){
        try{
            List<User> users = userService.viewAll();
            return ResponseEntity.status(HttpStatus.OK).body(users);
        }
        catch(Exception ex){
            return new ResponseEntity<>("NOT AUTHORIZED" + ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping("/{userId}/details")
    @PostAuthorize("returnObject.getBody().userId == 20240101")
    public ResponseEntity<?> viewUserDetails(@PathVariable long userId){
        User user1 =  userService.searchById(userId);
        if(user1 == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User Not Found");
        }
        else{
            return ResponseEntity.status(HttpStatus.OK).body(user1);
        }
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<String> deleteAccount(@PathVariable long userId){
        try {
            userService.deleteUser(userId);
            return new ResponseEntity<>("User Deleted Successfully !", HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Error, Cannot Delete User !" + e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
        }
    }

    @GetMapping("{userId}/accounts")
    public ResponseEntity<?>viewAccountsByUser(@PathVariable long userId){
        try{
            List<BankAccount> accounts = bankingFeign.viewAccountsByUser(userId).getBody();
            if(accounts == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No accounts found");
            }
            else {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(accounts);
            }
        }
        catch(Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No accounts found");
        }
    }


    @PostMapping("{userId}/accounts/open-account")
    public ResponseEntity<String>openAccount(@RequestBody BankAccount bankAccount,@PathVariable long userId){
        try{
            bankAccount.setUserId(userId);
            List<BankAccount> accounts = bankingFeign.viewAccountsByUser(userId).getBody();
            if(accounts == null) {
                BankAccount bankAccount1 = bankingFeign.openAccount(bankAccount).getBody();
                return new ResponseEntity<>("New Account opened Successfully\nAccount No: "
                        + bankAccount1.getAccountNo(), HttpStatus.OK);
            }
            else{
                int checkingCount = 0;
                for(BankAccount account: accounts) {
                    if (account.getAccountType().equalsIgnoreCase("checking")) {
                        checkingCount += 1;
                    }
                }
                if(accounts.size() >= 3){
                    return new ResponseEntity<>("FAILED TO OPEN ACCOUNT\nOnly 3 accounts permitted per user", HttpStatus.OK);
                }
                else if(bankAccount.getAccountType().equalsIgnoreCase("checking") &&  checkingCount >= 2){
                    return new ResponseEntity<>("FAILED TO OPEN ACCOUNT\nOnly 2 Checking accounts permitted per user", HttpStatus.OK);
                }
                else{
                    BankAccount bankAccount1 = bankingFeign.openAccount(bankAccount).getBody();
                    return new ResponseEntity<>("New Account opened Successfully\nAccount No: "
                            +bankAccount1.getAccountNo(), HttpStatus.OK);
                }
            }
        }
        catch(Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Error: "+ex.getMessage());
        }
    }
    @DeleteMapping("{userId}/accounts/{accountNo}/delete")
    public ResponseEntity<?>deleteAccount(@PathVariable long userId, @PathVariable String accountNo){
        try {
            BankAccount account =  bankingFeign.searchByAccountNo(accountNo).getBody();
            if(account == null){
                return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
            }

            else{
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime createdDate = account.getCreatedDate();
                long daysBetween = ChronoUnit.DAYS.between(createdDate,now);
                if(daysBetween <= 30){
                    return new ResponseEntity<>
                            ("ACCOUNT CREATED WITHIN 30 DAYS CANNOT BE DELETED\n" +
                                    "Days remaining to delete this account: "+(30-daysBetween), HttpStatus.OK);
                }
                else{
                    bankingFeign.deleteAccount(accountNo);
                    return new ResponseEntity<>("Account Deleted Successfully !", HttpStatus.OK);
                }
            }
        }
        catch (Exception e) {
            return new ResponseEntity<>("Error, Cannot Delete Account !" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("{userId}/accounts/{accountNo}/update-account")
    public ResponseEntity<?>updateAccount(@RequestBody BankAccount bankAccount,@PathVariable long userId, @PathVariable String accountNo) {
        try {
            BankAccount account = bankingFeign.searchByAccountNo(accountNo).getBody();
            int maxUpdatesPerMonth = 2;
            if (account == null) {
                return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
            }
            else {
                bankAccount.setUserId(userId);
                if (bankAccount.getOwnerName().equals(account.getOwnerName())) {
                    account.setNickName(bankAccount.getNickName());
                    bankingFeign.updateAccount(account, accountNo);
                    return new ResponseEntity<>("Nickname successfully updated!", HttpStatus.OK);
                }
                else {
                    userService.updateAccount(account,bankAccount);
                    LocalDateTime lastUpdatedTime = account.getLastModifiedDate();
                    if (lastUpdatedTime != null && userService.isSameMonth(lastUpdatedTime,LocalDateTime.now()) && account.getUpdateCount() >= maxUpdatesPerMonth) {
                        return ResponseEntity.badRequest().body("Your name can only be updated twice a month, please try next month");
                    }
                    else if (lastUpdatedTime != null && !userService.isSameMonth(lastUpdatedTime,LocalDateTime.now())) {
                        account.setUpdateCount(1);
                        bankingFeign.updateAccount(account, accountNo);
                        return new ResponseEntity<>("Owner details updated", HttpStatus.OK);
                    }
                    else {
                        account.setUpdateCount(account.getUpdateCount() + 1);
                        bankingFeign.updateAccount(account, accountNo);
                        return new ResponseEntity<>("Owner details updated", HttpStatus.OK);
                    }
                }
            }
        }
        catch (Exception e) {
            return new ResponseEntity<>("Error, Cannot Update Account !" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
