package com.deloittevg.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_details")
public class User {
    @TableGenerator(
            name = "user_id_gen",
            table = "id_generator",
            pkColumnName = "sequence_name",
            valueColumnName = "next_val",
            pkColumnValue = "user_id_seq",
            allocationSize = 1,
            initialValue = 20240100)
    @Id
    @GeneratedValue(generator = "user_id_gen",strategy= GenerationType.TABLE)
    private long userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String primaryLocation;
    private String currentAddress;
    private String permAddress;
    private String job;
    private String jobLocation;
    private String grossSalary;
    @Column(unique = true,nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String role;

    public User() {
        super();
    }

    public User(long userId, String firstName, String middleName, String lastName, String primaryLocation, String currentAddress, String permAddress, String job, String jobLocation, String grossSalary, String email, String password, String role) {
        this.userId = userId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.primaryLocation = primaryLocation;
        this.currentAddress = currentAddress;
        this.permAddress = permAddress;
        this.job = job;
        this.jobLocation = jobLocation;
        this.grossSalary = grossSalary;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String firstName, String middleName, String lastName, String primaryLocation, String currentAddress, String permAddress, String job, String jobLocation, String grossSalary, String email, String password, String role) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.primaryLocation = primaryLocation;
        this.currentAddress = currentAddress;
        this.permAddress = permAddress;
        this.job = job;
        this.jobLocation = jobLocation;
        this.grossSalary = grossSalary;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getPrimaryLocation() {
        return primaryLocation;
    }

    public void setPrimaryLocation(String primaryLocation) {
        this.primaryLocation = primaryLocation;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getPermAddress() {
        return permAddress;
    }

    public void setPermAddress(String permAddress) {
        this.permAddress = permAddress;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public String getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(String grossSalary) {
        this.grossSalary = grossSalary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
