package com.deloittevg.controller;

import com.deloittevg.entity.BankAccount;
import com.deloittevg.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bank")
public class BankController {
	
	@Autowired
	BankAccountService bankAccountService;
	
	@GetMapping("/accounts")
	public ResponseEntity<List<BankAccount>> viewAll(){
		List<BankAccount>  account  = bankAccountService.viewAllAccounts();
		if(account==null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	
		return ResponseEntity.status(HttpStatus.OK).body(account);
			
		}
	
	@PostMapping("/accounts/create")
	public ResponseEntity<BankAccount> createAccount(@RequestBody BankAccount account) {
		
		BankAccount account1= bankAccountService.createAccount(account);
		if(account1==null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
			return ResponseEntity.status(HttpStatus.OK).body(account1);
			
		}

	@GetMapping ("/accounts/{userId}")
	public ResponseEntity<List<BankAccount>> searchByUser(@PathVariable long userId) {
		List<BankAccount> account1 = bankAccountService.searchByUserId(userId);
		if(account1.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(account1);

	}

	@PutMapping("/accounts/{accountNo}/update")
	public ResponseEntity<BankAccount> updateAccount(@RequestBody BankAccount account,@PathVariable String accountNo) {
		account.setAccountNo(accountNo);
		BankAccount account1= bankAccountService.updateAccount(account);
		if(account1==null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
			return ResponseEntity.status(HttpStatus.OK).body(account1);
			
		}
	
	
	
	@DeleteMapping("/accounts/{accountNo}/delete")
	public ResponseEntity<String> deleteAccount(@PathVariable String accountNo){
		try {
			bankAccountService.deleteAccount(accountNo);
			return new ResponseEntity<>("Account Deleted Successfully !", HttpStatus.OK);
		}
		catch (Exception e) {
			return new ResponseEntity<>("Error, Cannot Delete Account !" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/accounts/{accountNo}")
	public ResponseEntity<BankAccount> searchByAccountNo(@PathVariable String accountNo) {
		
		BankAccount account= bankAccountService.findByAccountNo(accountNo);
		if(account==null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	
		return ResponseEntity.status(HttpStatus.OK).body(account);
			
		}

}
