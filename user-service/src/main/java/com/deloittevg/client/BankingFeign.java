package com.deloittevg.client;

import com.deloittevg.dummy.BankAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name ="BANK-SERVICE")
public interface BankingFeign {
    @GetMapping("/bank/accounts/user/{userId}")
    public List<BankAccount> viewAccountsByUser(@PathVariable long userId);

    @PostMapping("/bank/accounts/create")
    public BankAccount openAccount(BankAccount bankAccount);

    @DeleteMapping("/bank/accounts/{accountNo}")
    public String deleteAccount(@PathVariable String accountNo);

    @GetMapping("/bank/accounts/{accountNo}")
    public BankAccount searchByAccountNo(@PathVariable String accountNo);

    @PutMapping("/bank/accounts/{accountNo}/update")
    public BankAccount updateAccount(@RequestBody BankAccount account,@PathVariable String accountNo);
}
