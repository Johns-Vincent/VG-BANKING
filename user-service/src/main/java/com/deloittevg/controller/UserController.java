package com.deloittevg.controller;

import com.deloittevg.client.BankingFeign;
import com.deloittevg.dummy.BankAccount;
import com.deloittevg.entity.User;
import com.deloittevg.service.UserService;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/register")
    public ResponseEntity<?>registerUser(@RequestBody User user){
        try{
            User user1 = userService.registerOrUpdate(user);
            if(user1 != null){
                String message = "User successfully registered"+"\nUser Name: "
                        +user.getFirstName()+" "+user.getLastName()+"\nUser ID: "
                        +user.getUserId();
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(message);
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("User not registered");
            }
        }
        catch(Exception ex) {
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
    @PreAuthorize("hasAnyRole('ADMIN')")
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
    public ResponseEntity<String>viewAccountsByUser(@RequestBody BankAccount bankAccount,@PathVariable long userId){
        try{
            bankAccount.setUserId(userId);
            List<BankAccount> accounts = bankingFeign.openAccount(bankAccount).getBody();
        }
        catch(Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No accounts found");
        }
    }
}
