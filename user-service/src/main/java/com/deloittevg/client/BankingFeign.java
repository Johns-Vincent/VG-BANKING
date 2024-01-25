package com.deloittevg.client;

import com.deloittevg.dummy.BankAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name ="BANK-SERVICE")
public interface BankingFeign {
    @GetMapping("/bank/accounts/user/{userId}")
    public ResponseEntity<List<BankAccount>> viewAccountsByUser(@PathVariable long userId);

    @PostMapping("/bank/accounts/create")
    public ResponseEntity<BankAccount> openAccount(BankAccount bankAccount);

    @DeleteMapping("/bank/accounts/{accountNo}/delete")
    public ResponseEntity<String> deleteAccount(@PathVariable String accountNo);

    @GetMapping("/bank/accounts/{accountNo}")
    public ResponseEntity<BankAccount> searchByAccountNo(@PathVariable String accountNo);

    @PutMapping("/bank/accounts/{accountNo}/update")
    public ResponseEntity<?> updateAccount(@RequestBody BankAccount account,@PathVariable String accountNo);
}
