package com.deloittevg.controller;

import com.deloittevg.entity.BankAccount;
import com.deloittevg.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bank")
public class BankController {
	

	private final BankAccountService bankAccountService;

	public BankController(BankAccountService bankAccountService) {
		this.bankAccountService = bankAccountService;
	}

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

	@GetMapping ("/accounts/user/{userId}")
	public ResponseEntity<List<BankAccount>> searchByUser(@PathVariable long userId) {
		List<BankAccount> account1 = bankAccountService.searchByUserId(userId);
		if(account1.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(account1);

	}

	@PutMapping("/accounts/{accountNo}/update")
	public ResponseEntity<String> updateAccount(@RequestBody BankAccount account,@PathVariable String accountNo) {

		try {
			account.setAccountNo(accountNo);
			BankAccount account1 = bankAccountService.findByAccountNo(accountNo);
			if (account1 == null) {
				return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
			} else {
				account1.setAccountNo(accountNo);
				bankAccountService.updateAccount(account);
				return new ResponseEntity<>("Account updated successfully !"+account1.getCreatedDate(), HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Error, Cannot Update Account !" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@DeleteMapping("/accounts/{accountNo}/delete")
	public ResponseEntity<String> deleteAccount(@PathVariable String accountNo){
		try {
			BankAccount account =  bankAccountService.findByAccountNo(accountNo);
			if(account == null){
				return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
			}
			else{
				bankAccountService.deleteAccount(accountNo);
				return new ResponseEntity<>("Account Deleted Successfully !", HttpStatus.OK);
			}
		}
		catch (Exception e) {
			return new ResponseEntity<>("Error, Cannot Delete Account !" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/accounts/{accountNo}")
	public ResponseEntity<BankAccount> searchByAccountNo(@PathVariable String accountNo) {
		
		BankAccount account= bankAccountService.findByAccountNo(accountNo);
		if(account==null) {
			return ResponseEntity.status(HttpStatus.OK).build();
		}
	
		return ResponseEntity.status(HttpStatus.OK).body(account);
			
		}

}
