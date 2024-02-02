package com.deloittevg.service;

import com.deloittevg.entity.BankAccount;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BankAccountService {
	
	BankAccount createAccount(BankAccount account);
	BankAccount updateAccount(BankAccount account, String accountNo);
	List<BankAccount> viewAllAccounts();
	BankAccount findByAccountNo (String accountNo);
	void deleteAccount (String accountNo);
	List<BankAccount> searchByUserId(long userId);
	

}
