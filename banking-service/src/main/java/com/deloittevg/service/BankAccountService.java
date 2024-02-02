package com.deloittevg.service;

import com.deloittevg.entity.BankAccount;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BankAccountService {
	
	BankAccount createAccount(BankAccount account);
	List<BankAccount> viewAllAccounts();
	BankAccount findByAccountNo (String accountNo);
	void deleteAccount (String accountNo);
	List<BankAccount> searchByUserId(long userId);
	BankAccount updateAccount(BankAccount account, BankAccount updatedaccount);
	

}
