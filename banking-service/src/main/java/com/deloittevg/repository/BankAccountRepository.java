package com.deloittevg.repository;

import com.deloittevg.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String>{
    List<BankAccount> findByUserId(long userId);
}
