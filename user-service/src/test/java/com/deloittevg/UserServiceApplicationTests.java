package com.deloittevg;

import com.deloittevg.client.BankingFeign;
import com.deloittevg.controller.UserController;
import com.deloittevg.dummy.BankAccount;
import com.deloittevg.entity.SecurityUser;
import com.deloittevg.entity.User;
import com.deloittevg.repository.UserRepository;
import com.deloittevg.service.JpaUserDetailsService;
import com.deloittevg.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserServiceApplicationTests {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private BankingFeign bankingFeign;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	User user1 = new User(123,"Johns","","Vincent","Kannur","Benguluru","Kerala","Analyst","Benguluru","45000","jvk@gmail.com","great","ADMIN");
	User user2 = new User(124,"James","Thomas","Miller","Mumbai","Benguluru","Mumbai","Analyst","Benguluru","45000","jmay@gmail.com","hello","USER");
	User user3 = new User("James","Thomas","Miller","Mumbai","Benguluru","Mumbai","Analyst","Benguluru","45000","jmay@gmail.com","hello","USER");
	BankAccount bankAccount1 = new BankAccount("asdf123dsfg134as",104,"Domestic","SAVINGS","Personal","Johns","","Vincent","Mr",true,"Active","Phone","Open","Email",123,"Joe");
	BankAccount bankAccount2 = new BankAccount("achewihcjb246his",105,"Domestic","CHECKING","Personal","James","Thomas","Miller","Mr",true,"Active","Phone","Open","Email",124,"mays");
	BankAccount bankAccount3 = new BankAccount("beuef73j59shsi3f",106,"Domestic","CHECKING","Personal","Greg","Thomas","Joseph","Mr",true,"Active","Phone","Open","Email",125,"greg");

	@InjectMocks
	private UserServiceImpl userService;

	@InjectMocks
	private JpaUserDetailsService jpaUserDetailsService;

	@Autowired
	private UserController userController;

	@Test
	void testRegisterUser(){
		User user = this.user1;
		when(userRepository.save(user)).thenReturn(user);
		assertEquals(user,userService.registerUser(user));
		verify(userRepository).save(user);
	}
	@Test
	void testViewAll(){
		List<User> users = Arrays.asList(this.user1,this.user2);
		when(userRepository.findAll()).thenReturn(users);
		assertEquals(users,userService.viewAll());
		verify(userRepository).findAll();
	}

	@Test
	void testDeleteUser(){
		User user = this.user2;
		userService.deleteUser(user.getUserId());
		verify(userRepository,times(1)).deleteById(user.getUserId());
	}

	@Test
	void testSearchById(){
		User user = this.user1;
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		assertEquals(user,userService.searchById(user.getUserId()));
		verify(userRepository).findById(user.getUserId());
	}
	@Test
	void testSearchByEmail(){
		User user = this.user2;
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
		assertEquals(user,userService.searchByEmail(user.getEmail()));
		verify(userRepository).findByEmail(user.getEmail());
	}

	@Test
	void testUpdateUser() {
		User user = this.user1;
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);
		assertEquals(user,userService.updateUser(user,user.getUserId()));
		verify(userRepository).save(user);
	}

	@Test
	void testUpdateUser_UserNull() {
		User user = this.user1;
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        assertNull(userService.updateUser(user, 126));
	}
	@Test
	void testRegisterUser_EmailNull(){
		User user = this.user1;
		user.setEmail(null);
		assertThrows(IllegalArgumentException.class,()-> userService.registerUser(user));
	}

	@Test
	void testRegisterUser_Null(){
		User user = null;
		assertThrows(IllegalArgumentException.class,()-> userService.registerUser(user));
	}

	@Test
	void testRegisterUser_EmailDuplicate(){
		User user = this.user2;
		user.setEmail("jvk@gmail.com");
		when(userRepository.save(user1)).thenReturn(user1);
		when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);
		userService.registerUser(user1);
		assertThrows(IllegalArgumentException.class,()-> userService.registerUser(user));
	}

	@Test
	void testIsSameMonth(){
		assertEquals(true,userService.isSameMonth(LocalDateTime.now(),LocalDateTime.now()));
	}

	@Test
	void testUpdateAccountDetails(){
		BankAccount account = this.bankAccount1;
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(account);
		when(bankingFeign.updateAccount(account,account.getAccountNo())).thenReturn(account);
		assertEquals("Owner details updated",userService.updateAccount(account,user1.getUserId(),account.getAccountNo()).getBody());
	}

	@Test
	void testUpdateAccountDetails_AccountNull(){
		BankAccount account = this.bankAccount2;
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(null);
		assertEquals("Account not found",userService.updateAccount(account,user2.getUserId(),account.getAccountNo()).getBody());
	}

	@Test
	void testUpdateAccountDetails_SameMonth(){
		BankAccount account = this.bankAccount1;
		account.setUpdateCount(2);
		account.setLastModifiedDate(LocalDateTime.now());
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(account);
		assertEquals("Your name can only be updated twice a month, please try next month",userService.updateAccount(account,user1.getUserId(),account.getAccountNo()).getBody());
	}

	@Test
	void testUpdateAccountDetails_DifferentMonth(){
		BankAccount account = this.bankAccount1;
		account.setUpdateCount(2);
		account.setLastModifiedDate(LocalDateTime.now().minusMonths(1));
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(account);
		when(bankingFeign.updateAccount(account,account.getAccountNo())).thenReturn(account);
		assertEquals("Owner details updated",userService.updateAccount(account,user1.getUserId(),account.getAccountNo()).getBody());
	}

	@Test
	void testUpdateNickName(){
		BankAccount account = this.bankAccount1;
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(account);
		when(bankingFeign.updateAccount(account,account.getAccountNo())).thenReturn(account);
		assertEquals("Nickname updated",userService.updateNickName("Jo",account.getAccountNo()).getBody());
	}

	@Test
	void testUpdateNickName_AccountNull(){
		BankAccount account = this.bankAccount1;
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(null);
		assertEquals("Account not found",userService.updateNickName("Jo",account.getAccountNo()).getBody());
	}

	@Test
	void testDeleteAccount_MoreThanOneMonth(){
		BankAccount account = this.bankAccount1;
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(account);
		account.setCreatedDate(LocalDateTime.now().minusDays(31));
		assertEquals("Account deleted successfully",userService.deleteAccount(123,account.getAccountNo()).getBody());
	}

	@Test
	void testDeleteAccount_LessThanOneMonth(){
		BankAccount account = this.bankAccount1;
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(account);
		account.setCreatedDate(LocalDateTime.now().minusDays(29));
		assertEquals("ACCOUNT CREATED WITHIN 30 DAYS CANNOT BE DELETED\n"+
				"Days remaining to delete this account: 1",userService.deleteAccount(123,account.getAccountNo()).getBody());
	}

	@Test
	void testDeleteAccount_AccountNull(){
		BankAccount account = this.bankAccount1;
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(null);
		assertEquals("Account not found !",userService.deleteAccount(124,account.getAccountNo()).getBody());
	}

	@Test
	void testWelcomeUser(){
		User user = this.user1;
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
		assertEquals(user,userService.getUserFromAuth());
		assertEquals("Welcome " + user1.getFirstName()+ " " + user1.getLastName(),userService.welcomeUser());
		assertEquals("Welcome " + user1.getFirstName()+ " " + user1.getLastName(),userController.userLogin());
	}

	@Test
	void testViewAccountsByUser(){
		User user = this.user1;
		List<BankAccount> accounts = List.of(bankAccount1,bankAccount2);
		when(bankingFeign.viewAccountsByUser(user.getUserId())).thenReturn(accounts);
		assertEquals(accounts,userService.viewAccountsByUser(user.getUserId()));
	}

	@Test
	void testOpenAccount_AccountsNull(){
		User user = this.user1;
		BankAccount account = this.bankAccount1;
		when(bankingFeign.viewAccountsByUser(user.getUserId())).thenReturn(null);
		when(bankingFeign.openAccount(account)).thenReturn(account);
		assertEquals("New Account opened Successfully\nAccount No: "
				+ account.getAccountNo(),userService.openAccount(user.getUserId(),account).getBody());
	}
	@Test
	void testOpenAccount_ThreeAccounts(){
		User user = this.user1;
		BankAccount account = this.bankAccount1;
		List<BankAccount> accounts = List.of(bankAccount1,bankAccount2,bankAccount3);
		when(bankingFeign.viewAccountsByUser(user.getUserId())).thenReturn(accounts);
		assertEquals("FAILED TO OPEN ACCOUNT\nOnly 3 accounts permitted per user"
				,userService.openAccount(user.getUserId(),account).getBody());
	}

	@Test
	void testOpenAccount_TwoCheckingAccounts(){
		User user = this.user1;
		BankAccount account = this.bankAccount2;
		List<BankAccount> accounts = List.of(bankAccount2,bankAccount3);
		when(bankingFeign.viewAccountsByUser(user.getUserId())).thenReturn(accounts);
		assertEquals("FAILED TO OPEN ACCOUNT\nOnly 2 Checking accounts permitted per user"
				,userService.openAccount(user.getUserId(),account).getBody());
	}

	@Test
	void testOpenAccount_AccountNotNull(){
		User user = this.user1;
		BankAccount account = this.bankAccount2;
		List<BankAccount> accounts = List.of(bankAccount1,bankAccount2);
		when(bankingFeign.openAccount(account)).thenReturn(account);
		when(bankingFeign.viewAccountsByUser(user.getUserId())).thenReturn(accounts);
		assertEquals("New Account opened Successfully\nAccount No: "
						+ account.getAccountNo()
				,userService.openAccount(user.getUserId(),account).getBody());
	}

	@Test
	void testLoadByUsername(){
		User user = this.user1;
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
		assertEquals(user.getEmail(),jpaUserDetailsService.loadUserByUsername(user.getEmail()).getUsername());
	}

	@Test
	void testLoadByUsername_UserNull(){
		User user = this.user1;
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
		assertThrows(UsernameNotFoundException.class,()->jpaUserDetailsService.loadUserByUsername("nonexistent@gmail.com"));
	}
	@Test
	void testControllerRegisterUser(){
		User user = this.user1;
		when(userRepository.save(user)).thenReturn(user);
		assertEquals("User registered\nUser ID: "+ user.getUserId(),userController.registerUser(user).getBody());
	}

	@Test
	void testControllerRegisterUser_UserNull(){
		User user = null;
		assertEquals("User not registered: User cannot be null",userController.registerUser(user).getBody());
	}

	@Test
	void testControllerViewAll(){
		List<User> users = Arrays.asList(this.user1,this.user2);
		when(userRepository.findAll()).thenReturn(users);
		assertEquals(users,userController.viewAll().getBody());
	}

	@Test
	void testControllerViewAll_UsersNull(){
		when(userRepository.findAll()).thenReturn(null);
		assertEquals(null,userController.viewAll().getBody());
	}

	@Test
	void testControllerUpdateUser(){
		User user = this.user1;
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);
		assertEquals("User details of "+user.getFirstName()+" " + user.getLastName()
				+ " has been updated.",userController.updateUser(user,user.getUserId()).getBody());
	}

	@Test
	void testControllerUpdateUser_UserNull(){
		User user = this.user1;
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		assertEquals("No user found",userController.updateUser(user,200).getBody());
	}

	@Test
	void testControllerViewUserDetails(){
		User user = this.user1;
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		assertEquals(user,userController.viewUserDetails(user.getUserId()).getBody());
	}

	@Test
	void testControllerViewUserDetails_UserNull(){
		User user = this.user1;
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        assertNull(userController.viewUserDetails(200).getBody());
	}

	@Test
	void testControllerDeleteUser(){
		User user = this.user2;
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		assertEquals("User: "+ user.getUserId() +" deleted",userController.deleteAccount(user.getUserId()).getBody());
		verify(userRepository,times(1)).deleteById(user.getUserId());
	}

	@Test
	void testControllerDeleteUser_UserNull(){
		User user = this.user2;
		assertEquals("Account Not Found",userController.deleteAccount(user.getUserId()).getBody());
	}

	@Test
	void testControllerViewAccountsByUser(){
		User user = this.user1;
		List<BankAccount> accounts = List.of(bankAccount1,bankAccount2);
		when(bankingFeign.viewAccountsByUser(user.getUserId())).thenReturn(accounts);
		assertEquals(accounts,userController.viewAccountsByUser(user.getUserId()).getBody());
	}

	@Test
	void testControllerViewAccountsByUser_AccountsNull(){
		User user = this.user1;
		List<BankAccount> accounts = Collections.emptyList();
		when(bankingFeign.viewAccountsByUser(user.getUserId())).thenReturn(accounts);
		assertEquals(null,userController.viewAccountsByUser(user.getUserId()).getBody());
	}
	@Test
	void testControllerOpenAccount(){
		User user = this.user1;
		BankAccount account = this.bankAccount1;
		when(bankingFeign.viewAccountsByUser(user.getUserId())).thenReturn(null);
		when(bankingFeign.openAccount(account)).thenReturn(account);
		assertEquals("New Account opened Successfully\nAccount No: "
				+ account.getAccountNo(),userController.openAccount(account,user.getUserId()).getBody());
	}

	@Test
	void testControllerUpdateAccount(){
		User user = this.user1;
		BankAccount account = this.bankAccount1;
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(account);
		when(bankingFeign.updateAccount(account,account.getAccountNo())).thenReturn(account);
		assertEquals("Owner details updated",userController.updateAccount(account,user.getUserId(),account.getAccountNo()).getBody());
	}


	@Test
	void testControllerUpdateNickName(){
		User user = this.user1;
		BankAccount account = this.bankAccount1;
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(account);
		when(bankingFeign.updateAccount(account,account.getAccountNo())).thenReturn(account);
		assertEquals("Nickname updated",userController.updateNickName("Jo",user.getUserId(),account.getAccountNo()).getBody());
	}

	@Test
	void testControllerDeleteAccount(){
		User user = this.user1;
		BankAccount account = this.bankAccount1;
		when(bankingFeign.searchByAccountNo(account.getAccountNo())).thenReturn(account);
		account.setCreatedDate(LocalDateTime.now().minusDays(31));
		assertEquals("Account deleted successfully",userController.deleteAccount(user.getUserId(),account.getAccountNo()).getBody());
	}

	@Test
	void testUserGettersSetters(){
		User user = new User();
		user.setEmail("j@gmail.com");
		user.setUserId(123);
		user.setRole("ADMIN");
		user.setJob("Analyst");
		user.setFirstName("Johns");
		user.setCurrentAddress("Benguluru");
		user.setGrossSalary("45000");
		user.setJobLocation("Benguluru");
		user.setLastName("Vincent");
		user.setMiddleName("");
		user.setPassword("great");
		user.setPermAddress("Kerala");
		user.setPrimaryLocation("Benguluru");
		assertEquals("j@gmail.com",user.getEmail());
		assertEquals(123,user.getUserId());
		assertEquals("ADMIN",user.getRole());
		assertEquals("Benguluru",user.getCurrentAddress());
		assertEquals("Benguluru",user.getJobLocation());
		assertEquals("Analyst",user.getJob());
		assertEquals("Johns",user.getFirstName());
		assertEquals("45000",user.getGrossSalary());
		assertEquals("",user.getMiddleName());
		assertEquals("great",user.getPassword());
		assertEquals("Kerala",user.getPermAddress());
		assertEquals("Vincent",user.getLastName());
		assertEquals("Benguluru",user.getPrimaryLocation());
	}

	@Test
	void testBankAccountGettersSetters(){
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
		bankAccount.setOwnerName("Johns Vincent");
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
		assertEquals("Mr Johns  Vincent",bankAccount.getOwnerName());
	}

	@Test
	void testSecurityUserMethods(){
		User user = this.user1;
		SecurityUser securityUser = new SecurityUser(user);
		assertEquals("jvk@gmail.com",securityUser.getUsername());
        assertTrue(passwordEncoder().matches("great", securityUser.getPassword()));
		assertEquals("[ROLE_ADMIN]",securityUser.getAuthorities().toString());
        assertTrue(securityUser.isAccountNonExpired());
		assertTrue(securityUser.isEnabled());
		assertTrue(securityUser.isCredentialsNonExpired());
		assertTrue(securityUser.isAccountNonLocked());
	}

	@Test
	void applicationContextTest() {
		UserServiceApplication.main(new String[]{});
	}














}
