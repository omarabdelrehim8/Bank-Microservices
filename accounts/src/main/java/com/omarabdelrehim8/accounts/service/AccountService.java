package com.omarabdelrehim8.accounts.service;

import com.omarabdelrehim8.accounts.dto.AccountDto;
import com.omarabdelrehim8.accounts.dto.CustomerDto;

import java.util.List;

public interface AccountService {

    /**
     * Creates New Account based on New Customer's details
     *
     * @param customerDto - CustomerDto Object
     */
    void createAccountForNewCustomer(CustomerDto customerDto);

    /**
     * Adds New Account for Existing Customer
     *
     * @param customerDto - CustomerDto Object
     */
    void addAccountForExistingCustomer(CustomerDto customerDto);

    /**
     * Fetches Accounts Details
     *
     * @param mobileNumber - Input Mobile Number
     * @return Accounts details based on a given mobileNumber
     */
    List<AccountDto> fetchAccountsDetails(String mobileNumber);


    /**
     * Delete Account
     *
     * @param accountNumber - Input Account Number
     * @return boolean based off of the outcome of the operation
     */
    boolean deleteAccount(Long accountNumber);

    /**
     * Fetches Customer Details
     *
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    CustomerDto fetchCustomerDetails(String mobileNumber);

    /**
     * Updates Customer Details
     *
     * @param customerDto - UpdatedCustomerDetailsDto Object
     * @return A boolean based off of the outcome of the operation
     */
    boolean updateCustomerDetails(CustomerDto customerDto);
}
