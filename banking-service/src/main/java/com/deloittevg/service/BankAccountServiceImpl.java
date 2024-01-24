package com.deloittevg.service;

import com.deloittevg.entity.BankAccount;
import com.deloittevg.repository.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService{

	@Autowired
	BankAccountRepository bankAccountRepository;
	@Override
	public BankAccount createAccount(BankAccount account) {
		// TODO Auto-generated method stub
		return bankAccountRepository.save(account);
	}

	@Override
	public BankAccount updateAccount(BankAccount account) {
		// TODO Auto-generated method stub
		return bankAccountRepository.save(account);
	}

	@Override
	public List<BankAccount> viewAllAccounts() {
		// TODO Auto-generated method stub
		return bankAccountRepository.findAll();
	}

	@Override
	public BankAccount findByAccountNo(String accountNo) {
		// TODO Auto-generated method stub
		return bankAccountRepository.findById(accountNo).orElse(null);
	}

	@Transactional
	public void deleteAccount(String accountNo) {
		// TODO Auto-generated method stub
		bankAccountRepository.deleteById(accountNo);
	}

	@Override
	public List<BankAccount> searchByUserId(long userId) {
		return bankAccountRepository.findByUserId(userId);
	}

}
