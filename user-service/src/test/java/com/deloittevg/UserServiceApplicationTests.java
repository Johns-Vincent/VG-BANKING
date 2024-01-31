package com.deloittevg;

import com.deloittevg.entity.User;
import com.deloittevg.repository.UserRepository;
import com.deloittevg.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserServiceApplicationTests {

	@MockBean
	private UserRepository userRepository;

	@Autowired
	private UserService userService;
	@Test
	void saveUserTest(){
		User user = new User(123,"Johns","","Vincent","Kannur","Benguluru","Kerala","Analyst","Benguluru","45000","jvk@gmail.com","great","ADMIN");
		when(userRepository.save(user)).thenReturn(user);
		assertEquals(user,userService.registerOrUpdate(user));
		verify(userRepository).save(user);
	}
	@Test
	void getUsersTest(){
		List<User> users = Arrays.asList(new User(123,"Johns","","Vincent","Kannur","Benguluru","Kerala","Analyst","Benguluru","45000","jvk@gmail.com","great","ADMIN"),
				new User(124,"James","Thomas","Miller","Mumbai","Benguluru","Mumbai","Analyst","Benguluru","45000","jmay@gmail.com","hello","USER"));
		when(userRepository.findAll()).thenReturn(users);
		assertEquals(users,userService.viewAll());
		verify(userRepository).findAll();
	}

	@Test
	void deleteUserTest(){
		User user = new User(123,"Johns","","Vincent","Kannur","Benguluru","Kerala","Analyst","Benguluru","45000","jvk@gmail.com","great","ADMIN");
		userService.deleteUser(user.getUserId());
		verify(userRepository,times(1)).deleteById(user.getUserId());
	}

	@Test
	void searchByUserIdTest(){
		User user = new User(123,"Johns","","Vincent","Kannur","Benguluru","Kerala","Analyst","Benguluru","45000","jvk@gmail.com","great","ADMIN");
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		assertEquals(user,userService.searchById(user.getUserId()));
		verify(userRepository).findById(user.getUserId());
	}
	@Test
	void searchByEmailTest(){
		User user = new User(123,"Johns","","Vincent","Kannur","Benguluru","Kerala","Analyst","Benguluru","45000","jvk@gmail.com","great","ADMIN");
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
		assertEquals(user,userService.searchByEmail(user.getEmail()));
		verify(userRepository).findByEmail(user.getEmail());

	}

}
