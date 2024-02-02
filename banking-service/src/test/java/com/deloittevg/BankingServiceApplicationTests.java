package com.deloittevg;

import com.deloittevg.entity.BankAccount;
import com.deloittevg.repository.BankAccountRepository;
import com.deloittevg.service.BankAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BankingServiceApplicationTests {


	BankAccount bankAccount1 = new BankAccount("asdf123dsfg134as",104,"Domestic","Checking","Personal","Johns","James","Jim","Jo","Mr",true,"Active","Phone","Open","Email",2024101);
	BankAccount bankAccount2 = new BankAccount("asdf123dsfg134as",104,"Domestic","Checking","Personal","Johns","James","Jim","Jo","Mr",true,"Active","Phone","Open","Email",2024102);
	@MockBean
	private BankAccountRepository bankAccountRepository;

	@Autowired
	private BankAccountService bankAccountService;

	@Test
	void createAccountTest() {
		BankAccount bankAccount = this.bankAccount1;
		when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
		assertEquals(bankAccount,bankAccountService.createAccount(bankAccount));
		verify(bankAccountRepository).save(bankAccount);
	}

	@Test
	void viewAllAccountsTest(){
		List<BankAccount> bankAccounts = Arrays.asList(this.bankAccount1,this.bankAccount2);
		when(bankAccountRepository.findAll()).thenReturn(bankAccounts);
		assertEquals(bankAccounts,bankAccountService.viewAllAccounts());
		verify(bankAccountRepository).findAll();
	}

	@Test
	void findByAccountNoTest(){
		BankAccount bankAccount = this.bankAccount1;
		when(bankAccountRepository.findById(bankAccount.getAccountNo())).thenReturn(Optional.of(bankAccount));
		assertEquals(bankAccount,bankAccountService.findByAccountNo(bankAccount.getAccountNo()));
		verify(bankAccountRepository).findById(bankAccount.getAccountNo());
	}

	@Test
	void searchByUserIdTest(){
		List<BankAccount> bankAccounts = Arrays.asList(this.bankAccount1,this.bankAccount2);
		when(bankAccountRepository.findByUserId(2024101)).thenReturn(bankAccounts);
		assertEquals(bankAccounts,bankAccountService.searchByUserId(2024101));
		verify(bankAccountRepository).findByUserId(2024101);
	}

	@Test
	void deleteAccountTest(){
		BankAccount bankAccount = this.bankAccount2;
		bankAccountService.deleteAccount(bankAccount.getAccountNo());
		verify(bankAccountRepository,times(1)).deleteById(bankAccount.getAccountNo());
	}

}
