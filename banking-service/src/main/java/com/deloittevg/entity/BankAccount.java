package com.deloittevg.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="bankacc_table")
public class BankAccount {
	@TableGenerator(
			name="acc_no_gen",
			table = "tbl_acc_no",
			pkColumnName = "accNo",
			pkColumnValue = "acc",
			valueColumnName = "acc_value",
			allocationSize = 1)
	@Id
	private String accountNo;
	private long bankId;
	private String bankType;
	private String accountType;
	private String accountOwnerType;
	private String firstName;
	private String middleName;
	private String lastName;
	private String suffix;
	private boolean primaryBank;
	private String status;
	private String authenticationMethod;
	private String transactionType;
	private String communicationChannel;
	private long userId;
	
	
	 @PrePersist
	    private void generaAccountNo() {
	        // Generate a 16-character alphanumeric user ID
	        this.accountNo = generateRandomAccountNo();
	    }

	    private String generateRandomAccountNo() {
	        // Generate a random UUID and remove hyphens to get a 32-character string
	        String uuid = UUID.randomUUID().toString().replace("-", "");
	        
	        // Take the first 16 characters
	        return uuid.substring(0, 16);
	    }

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public BankAccount() {
	}

	public BankAccount(String accountNo, long bankId, String bankType, String accountType, String accountOwnerType, String firstName, String middleName, String lastName, String suffix, boolean primaryBank, String status, String authenticationMethod, String transactionType, String communicationChannel, long customerId) {
		this.accountNo = accountNo;
		this.bankId = bankId;
		this.bankType = bankType;
		this.accountType = accountType;
		this.accountOwnerType = accountOwnerType;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.suffix = suffix;
		this.primaryBank = primaryBank;
		this.status = status;
		this.authenticationMethod = authenticationMethod;
		this.transactionType = transactionType;
		this.communicationChannel = communicationChannel;
		this.userId = customerId;
	}

	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public long getBankId() {
		return bankId;
	}
	public void setBankId(long bankId) {
		this.bankId = bankId;
	}
	public String getBankType() {
		return bankType;
	}
	public void setBankType(String bankType) {
		this.bankType = bankType;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getAccountOwnerType() {
		return accountOwnerType;
	}
	public void setAccountOwnerType(String accountOwnerType) {
		this.accountOwnerType = accountOwnerType;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public boolean isPrimaryBank() {
		return primaryBank;
	}
	public void setPrimaryBank(boolean primaryBank) {
		this.primaryBank = primaryBank;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAuthenticationMethod() {
		return authenticationMethod;
	}
	public void setAuthenticationMethod(String authenticationMethod) {
		this.authenticationMethod = authenticationMethod;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getCommunicationChannel() {
		return communicationChannel;
	}
	public void setCommunicationChannel(String communicationChannel) {
		this.communicationChannel = communicationChannel;
	}
	
	
	

}
