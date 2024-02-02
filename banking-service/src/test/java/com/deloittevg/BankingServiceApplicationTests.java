package com.deloittevg;

import com.deloittevg.controller.BankController;
import com.deloittevg.entity.BankAccount;
import com.deloittevg.repository.BankAccountRepository;
import com.deloittevg.service.BankAccountService;
import com.deloittevg.service.BankAccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BankingServiceApplicationTests {


	BankAccount bankAccount1 = new BankAccount("asdf123dsfg134as",104,"Domestic","Checking","Personal","Johns","James","Jim","Jo","Mr",true,"Active","Phone","Open","Email",20240101);
	BankAccount bankAccount2 = new BankAccount("asdf123dsfg134as",104,"Domestic","Checking","Personal","Johns","James","Jim","Jo","Mr",true,"Active","Phone","Open","Email",20240101);
	@MockBean
	private BankAccountRepository bankAccountRepository;

	@Autowired
	private BankController bankController;

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
	void updateAccountTest() {
		BankAccount bankAccount = this.bankAccount1;
		when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
		assertEquals(bankAccount,bankAccountService.updateAccount(bankAccount,bankAccount.getAccountNo()));
		verify(bankAccountRepository).save(bankAccount);
	}

	@Test
	void updateAccountTest_ifAccountIsNull() {
		BankAccount bankAccount = null;
		assertThrows(IllegalArgumentException.class,()->bankAccountService.updateAccount(bankAccount,null));
	}

	@Test
	void viewAllAccountsTest(){
		List<BankAccount> bankAccounts = Arrays.asList(this.bankAccount1,this.bankAccount2);
		when(bankAccountRepository.findAll()).thenReturn(bankAccounts);
		assertEquals(bankAccounts,bankAccountService.viewAllAccounts());
		verify(bankAccountRepository).findAll();
	}

	@Test
	void viewAllAccountsTest_ifListOfAccountsIsNull(){
		List<BankAccount> bankAccounts = Collections.emptyList();
		when(bankAccountRepository.findAll()).thenReturn(bankAccounts);
		assertEquals(Collections.emptyList(),bankAccountService.viewAllAccounts());
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
		when(bankAccountRepository.findByUserId(20240101)).thenReturn(bankAccounts);
		assertEquals(bankAccounts,bankAccountService.searchByUserId(20240101));
		verify(bankAccountRepository).findByUserId(20240101);
	}

	@Test
	void searchByUserIdTest_ifUserHasNoAccount(){
		List<BankAccount> bankAccounts = Collections.emptyList();
		when(bankAccountRepository.findByUserId(20240101)).thenReturn(bankAccounts);
		assertEquals(Collections.emptyList(),bankAccountService.searchByUserId(20240101));
		verify(bankAccountRepository).findByUserId(20240101);
	}

	@Test
	void deleteAccountTest(){
		BankAccount bankAccount = this.bankAccount2;
		bankAccountService.deleteAccount(bankAccount.getAccountNo());
		verify(bankAccountRepository,times(1)).deleteById(bankAccount.getAccountNo());
	}

	@Test
	void createAccount_ControllerTest(){
		BankAccount entity = this.bankAccount1;
		when(bankAccountService.createAccount(any(BankAccount.class))).thenReturn(entity);
		BankAccount createdAccount = bankController.createAccount(entity);
		assertNotNull(createdAccount);
		assertEquals(entity.getAccountNo(),createdAccount.getAccountNo());
	}

	@Test
	void updateAccount_ControllerTest(){
		BankAccount entity = this.bankAccount1;
		entity.setUserId(20240102);
		when(bankAccountService.updateAccount(entity,entity.getAccountNo())).thenReturn(entity);
		BankAccount updatedAccount = bankController.updateAccount(entity,entity.getAccountNo());
		assertNotNull(updatedAccount);
		assertEquals(entity,updatedAccount);
	}

	@Test
	void searchByUserId_ControllerTest(){
		List<BankAccount> bankAccounts = Arrays.asList(this.bankAccount1,this.bankAccount2);
		when(bankAccountService.searchByUserId(20240101)).thenReturn(bankAccounts);
		List<BankAccount> userAccounts = bankController.searchByUser(20240101);
		assertEquals(bankAccounts.size(),userAccounts.size());
		assertEquals(bankAccounts,userAccounts);
	}

	@Test
	void viewAllAccounts(){
		List<BankAccount> bankAccounts = Arrays.asList(this.bankAccount1,this.bankAccount2);
		when(bankAccountService.viewAllAccounts()).thenReturn(bankAccounts);
		List<BankAccount> userAccounts = bankController.viewAll();
		assertEquals(bankAccounts,userAccounts);
	}

//	@Test
//	void searchByAccountNo_ControllerTest(){
//		BankAccount bankAccount = this.bankAccount1;
//		when(bankAccountService.findByAccountNo("asdf123dsfg134as")).thenReturn(bankAccount);
//		fetchedAccount = bankController.searchByAccountNo("asdf123dsfg134as");
//		assertEquals(fetchedAccount,bankAccount);
//	}

//	@Test
//	void deleteAccount_ControllerTest(){
//		BankAccount bankAccount = this.bankAccount1;
//		String expected ="";
//		when(bankAccountService.deleteAccount(bankAccount.getAccountNo()),doNothing();
//		String message = bankController.deleteAccount(bankAccount.getAccountNo());
//	}
//
	@Test
	void bankAccountConstructorAndGettersTest(){
		BankAccount bankAccount = new BankAccount();
		bankAccount.setAccountNo(bankAccount1.getAccountNo());
		bankAccount.setBankId(bankAccount1.getBankId());
		bankAccount.setBankType(bankAccount1.getBankType());
		bankAccount.setAccountType(bankAccount1.getAccountType());
		bankAccount.setAccountOwnerType(bankAccount1.getAccountOwnerType());
		bankAccount.setFirstName(bankAccount1.getFirstName());
		bankAccount.setMiddleName(bankAccount1.getMiddleName());
		bankAccount.setLastName(bankAccount1.getLastName());
		bankAccount.setSuffix(bankAccount1.getSuffix());
		bankAccount.setPrimaryBank(bankAccount1.isPrimaryBank());
		bankAccount.setStatus(bankAccount1.getStatus());
		bankAccount.setAuthenticationMethod(bankAccount1.getAuthenticationMethod());
		bankAccount.setTransactionType(bankAccount1.getTransactionType());
		bankAccount.setCommunicationChannel(bankAccount1.getCommunicationChannel());
		bankAccount.setUserId(bankAccount1.getUserId());
		bankAccount.setNickName(bankAccount1.getNickName());

//		BankAccount bankAccountnew = new BankAccount(accountNo,bankId,bankType,accountType,accountOwnerType,firstName,middleName,lastName,nickName,suffix,primaryBank,status,authenticationMethod,transactionType,communicationChannel,userId);

		assertNotNull(bankAccount);
		assertEquals(bankAccount.getAccountNo(),bankAccount1.getAccountNo());
		assertEquals(bankAccount1.getBankId(),bankAccount.getBankId());
		assertEquals(bankAccount1.getBankType(),bankAccount.getBankType());
	}

}
