package com.deloittevg.service;

import com.deloittevg.entity.BankAccount;
import com.deloittevg.repository.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService{

	@Autowired
	BankAccountRepository bankAccountRepository;
	
	private static final int NAME_UPDATE_LIMIT = 2;
	
	@Override
	public BankAccount createAccount(BankAccount account) {
		// TODO Auto-generated method stub
		return bankAccountRepository.save(account);
	}

	@Override
	public BankAccount updateAccount(BankAccount account, BankAccount updatedaccount) {
		// TODO Auto-generated method stub
		if (isNicknameUpdate(account, updatedaccount)&& isOwnernameUpdate(account, updatedaccount))
			return bankAccountRepository.save(account);
		else
			throw new RuntimeException("Update NOT Allowed");
	}

	private boolean isOwnernameUpdate(BankAccount account, BankAccount updatedaccount) {
		// TODO Auto-generated method stub
		LocalDate todayy = LocalDate.now();
		if(todayy.getMonth() == account.getLastOwnernameUpdate().getMonth() && account.getOwnernameUpdateCount()>= NAME_UPDATE_LIMIT)
			throw new RuntimeException("Owner name can be updated only twice in a month");
		return true;
	}

	private boolean isNicknameUpdate(BankAccount account, BankAccount updatedaccount) {
		// TODO Auto-generated method stub
		return account.getNickname().equals(updatedaccount.getNickname());
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
