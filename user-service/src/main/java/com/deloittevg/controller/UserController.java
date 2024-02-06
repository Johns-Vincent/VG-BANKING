package com.deloittevg.controller;

import com.deloittevg.dummy.BankAccount;
import com.deloittevg.entity.User;
import com.deloittevg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.util.List;
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String userLogin(){
        return  userService.welcomeUser();
    }

    @PostMapping("/register")
    public ResponseEntity<String>registerUser(@RequestBody User user) {
        try{
            User user1 = userService.registerUser(user);
            return new ResponseEntity<>("User registered\nUser ID: "+ user1.getUserId(),HttpStatus.OK);
        }
        catch(IllegalArgumentException ex){
            return new ResponseEntity<>("User not registered: "+ex.getMessage(),HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<String> updateUser(@RequestBody User user, @PathVariable long userId){
        if(userService.updateUser(user,userId) != null){
            return new ResponseEntity<>("User details of "+user.getFirstName()+" " + user.getLastName()
            + " has been updated.",HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("No user found",HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> viewAll(){
        if(userService.viewAll() != null) {
            return new ResponseEntity<>(userService.viewAll(), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/{userId}/details")
    public ResponseEntity<User> viewUserDetails(@PathVariable long userId){
        User user1 =  userService.searchById(userId);
        if(user1 == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
            return ResponseEntity.status(HttpStatus.OK).body(user1);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteAccount(@PathVariable long userId){
        if(userService.searchById(userId) != null){
            userService.deleteUser(userId);
            return new ResponseEntity<>("User: "+ userId +" deleted",HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Account Not Found",HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{userId}/accounts")
    public ResponseEntity<List<BankAccount>>viewAccountsByUser(@PathVariable long userId){
        List<BankAccount> accounts = userService.viewAccountsByUser(userId);
        if(accounts.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(accounts);
        }
    }


    @PostMapping("{userId}/accounts/open-account")
    public ResponseEntity<String>openAccount(@RequestBody BankAccount bankAccount,@PathVariable long userId){
        return userService.openAccount(userId,bankAccount);
    }

    @DeleteMapping("{userId}/accounts/{accountNo}")
    public ResponseEntity<String> deleteAccount(@PathVariable long userId, @PathVariable String accountNo){
        return userService.deleteAccount(userId,accountNo);
    }

    @PutMapping("{userId}/accounts/{accountNo}/update-account")
    public ResponseEntity<String>updateAccount(@RequestBody BankAccount bankAccount,@PathVariable long userId, @PathVariable String accountNo) {
        return userService.updateAccount(bankAccount,userId,accountNo);
    }

    @PutMapping("{userId}/accounts/{accountNo}/update-nickname")
    public ResponseEntity<String>updateNickName(@RequestBody String nickName,@PathVariable long userId, @PathVariable String accountNo){
        return userService.updateNickName(nickName,accountNo);
    }
}
