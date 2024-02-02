package com.deloittevg.controller;

import com.deloittevg.entity.BankAccount;
import com.deloittevg.service.BankAccountService;

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
	public List<BankAccount> viewAll() {
		return bankAccountService.viewAllAccounts();
	}
	
	@PostMapping("/accounts/create")
	public BankAccount createAccount(@RequestBody BankAccount account) {
		return bankAccountService.createAccount(account);
	}
	@GetMapping ("/accounts/user/{userId}")
	public List<BankAccount> searchByUser(@PathVariable long userId) {
		return bankAccountService.searchByUserId(userId)
;	}

	@PutMapping("/accounts/{accountNo}/update")
	public BankAccount updateAccount(@RequestBody BankAccount account,@PathVariable String accountNo) {
		return bankAccountService.updateAccount(account,accountNo);
	}


	@DeleteMapping("/accounts/{accountNo}")
	public String deleteAccount(@PathVariable String accountNo){
		if(bankAccountService.findByAccountNo(accountNo)!= null){
			bankAccountService.deleteAccount(accountNo);
			return "Account: "+ accountNo +" deleted";
		}
		else {
			return "No account found";
		}
	}
	
	@GetMapping("/accounts/{accountNo}")
	public BankAccount searchByAccountNo(@PathVariable String accountNo) {
		return bankAccountService.findByAccountNo(accountNo);
	}

}
