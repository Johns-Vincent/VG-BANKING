package com.deloittevg.controller;

import com.deloittevg.client.BankingFeign;
import com.deloittevg.dummy.BankAccount;
import com.deloittevg.entity.User;
import com.deloittevg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/login")
    public String userLogin(){
        return  userService.welcomeUser();
    }

    @GetMapping("/details")
    public RedirectView redirectToConsole() {
        User user = userService.getUserFromAuth();
        return new RedirectView("/user/" + user.getUserId() + "/details");
    }

    @PostMapping("/register")
    public ResponseEntity<String>registerUser(@RequestBody User user) {
        try{
            User user1 = userService.registerUser(user);
            return new ResponseEntity<>("User registered\nUser ID: "+ user1.getUserId(),HttpStatus.OK);
        }
        catch(IllegalArgumentException ex){
            return new ResponseEntity<>("User not registered: "+ex.getMessage(),HttpStatus.NOT_IMPLEMENTED);
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
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
    @GetMapping("/{userId}/details")
//    @PostAuthorize("returnObject.getBody().userId == @userService.getUser().getUserId()")
    public ResponseEntity<?> viewUserDetails(@PathVariable long userId){
        User user1 =  userService.searchById(userId);
        if(user1 == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User Not Found");
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
    public ResponseEntity<?>viewAccountsByUser(@PathVariable long userId){
        return userService.viewAccountsByUser(userId);
    }


    @PostMapping("{userId}/accounts/open-account")
    public ResponseEntity<?>openAccount(@RequestBody BankAccount bankAccount,@PathVariable long userId){
        return userService.openAccount(userId,bankAccount);
    }

    @DeleteMapping("{userId}/accounts/{accountNo}")
    public ResponseEntity<?>deleteAccount(@PathVariable long userId, @PathVariable String accountNo){
        return userService.deleteAccount(userId,accountNo);
    }

    @PutMapping("{userId}/accounts/{accountNo}/update-account")
    public ResponseEntity<?>updateAccount(@RequestBody BankAccount bankAccount,@PathVariable long userId, @PathVariable String accountNo) {
        return userService.updateAccount(bankAccount,userId,accountNo);
    }

}
