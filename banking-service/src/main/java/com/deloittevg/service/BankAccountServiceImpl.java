package com.deloittevg.service;

import com.deloittevg.entity.BankAccount;
import com.deloittevg.repository.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService{

	@Autowired
	BankAccountRepository bankAccountRepository;
	@Override
	public BankAccount createAccount(BankAccount account) {
		return bankAccountRepository.save(account);
	}

	@Override
	public BankAccount updateAccount(BankAccount account, String accountNo) {
		if(account != null){
			account.setAccountNo(accountNo);
			return bankAccountRepository.save(account);
		}
		else{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public List<BankAccount> viewAllAccounts() {
		List<BankAccount> accounts = bankAccountRepository.findAll();
		if (accounts.isEmpty()) {
			return Collections.emptyList();
		}
		else {
			return accounts;
		}
	}

	@Override
	public BankAccount findByAccountNo(String accountNo) {
		return bankAccountRepository.findById(accountNo).orElse(null);
	}

	@Override
	public void deleteAccount(String accountNo) {
		bankAccountRepository.deleteById(accountNo);
	}
	@Override
	public List<BankAccount> searchByUserId(long userId) {
		List<BankAccount> accounts = bankAccountRepository.findByUserId(userId);
		if(accounts.isEmpty()) {
			return Collections.emptyList();
		}
		else{
			return accounts;
		}
	}
}
