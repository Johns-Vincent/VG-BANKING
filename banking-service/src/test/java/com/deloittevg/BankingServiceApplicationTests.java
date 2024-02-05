package com.deloittevg;

import com.deloittevg.controller.BankController;
import com.deloittevg.entity.BankAccount;
import com.deloittevg.repository.BankAccountRepository;
import com.deloittevg.service.BankAccountService;
import com.deloittevg.service.BankAccountServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BankingServiceApplicationTests {


	BankAccount bankAccount1 = new BankAccount("asdf123dsfg134as",104,"Domestic","Checking","Personal","Johns","James","Jim","Jo","Mr",true,"Active","Phone","Open","Email",20240101);
	BankAccount bankAccount2 = new BankAccount("asdf123dsfg134as",104,"Domestic","Checking","Personal","Johns","James","Jim","Jo","Mr",true,"Active","Phone","Open","Email",20240101);

	@MockBean
	private BankAccountRepository bankAccountRepository;

	@Autowired
	private BankController bankController;

	@InjectMocks
	private BankAccountServiceImpl bankAccountService;


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

	@Test
	void searchByAccountNo_ControllerTest(){
		BankAccount bankAccount = this.bankAccount1;
		when(bankAccountRepository.findById(bankAccount.getAccountNo())).thenReturn(Optional.of(bankAccount));
		assertEquals(bankAccount,bankController.searchByAccountNo(bankAccount.getAccountNo()));
	}

	@Test
	void deleteAccount_ControllerTest(){
		BankAccount bankAccount = this.bankAccount1;
		when(bankAccountRepository.findById(bankAccount.getAccountNo())).thenReturn(Optional.of(bankAccount));
		assertEquals("Account: "+ bankAccount.getAccountNo() +" deleted"
				,bankController.deleteAccount(bankAccount.getAccountNo()));
	}

	@Test
	void deleteAccount_ControllerTest_AccountNull(){
		BankAccount bankAccount = this.bankAccount1;
		when(bankAccountRepository.findById(bankAccount.getAccountNo())).thenReturn(Optional.of(bankAccount));
		assertEquals("No account found"
				,bankController.deleteAccount("NonExistentAccountNo"));
	}



	@Test
	void bankAccountGettersSettersTest(){
		BankAccount bankAccount = new BankAccount();
		bankAccount.setAccountNo("abcd");
		bankAccount.setBankId(123);
		bankAccount.setBankType("Domestic");
		bankAccount.setAccountType("Savings");
		bankAccount.setAccountOwnerType("Personal");
		bankAccount.setFirstName("Johns");
		bankAccount.setMiddleName("");
		bankAccount.setLastName("Vincent");
		bankAccount.setSuffix("Mr");
		bankAccount.setPrimaryBank(true);
		bankAccount.setStatus("Active");
		bankAccount.setAuthenticationMethod("Phone");
		bankAccount.setTransactionType("Open");
		bankAccount.setCommunicationChannel("Email");
		bankAccount.setUserId(123);
		bankAccount.setNickName("Jo");
		bankAccount.setCreatedDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(1,15)));
		bankAccount.setLastModifiedDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(1,15)));
		bankAccount.setUpdateCount(2);
		assertEquals("abcd",bankAccount.getAccountNo());
		assertEquals(123,bankAccount.getBankId());
		assertEquals("Domestic",bankAccount.getBankType());
		assertEquals("Savings",bankAccount.getAccountType());
		assertEquals("Personal",bankAccount.getAccountOwnerType());
		assertEquals("Johns",bankAccount.getFirstName());
		assertEquals("",bankAccount.getMiddleName());
		assertEquals("Vincent",bankAccount.getLastName());
		assertEquals("Mr",bankAccount.getSuffix());
		assertTrue(bankAccount.isPrimaryBank());
		assertEquals("Active",bankAccount.getStatus());
		assertEquals("Phone",bankAccount.getAuthenticationMethod());
		assertEquals("Open",bankAccount.getTransactionType());
		assertEquals("Email",bankAccount.getCommunicationChannel());
		assertEquals(123,bankAccount.getUserId());
		assertEquals("Jo",bankAccount.getNickName());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(1,15)),bankAccount.getCreatedDate());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(1,15)),bankAccount.getLastModifiedDate());
		assertEquals(2,bankAccount.getUpdateCount());
	}

	@Test
	void applicationContextTest() {
		BankingServiceApplication.main(new String[]{});
	}

	@Test
	void randomIdGeneratorTest(){
		BankAccount account = new BankAccount(104,"Domestic","Checking","Personal","Johns","James","Jim","Jo","Mr",true,"Active","Phone","Open","Email",20240101);
		String mockedUuid = account.generateRandomAccountNo();
		BankAccount account1 = Mockito.spy(account);
		Mockito.doReturn(mockedUuid).when(account1).generateRandomAccountNo();
		account1.generaAccountNo();
		assertEquals(mockedUuid, account1.getAccountNo());
	}

}
