package com.deloittevg.client;

import com.deloittevg.dummy.BankAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name ="BANK-SERVICE")
public interface BankingFeign {
    @GetMapping("/bank/accounts/user/{userId}")
    public ResponseEntity<List<BankAccount>> viewAccountsByUser(@PathVariable long userId);
}
