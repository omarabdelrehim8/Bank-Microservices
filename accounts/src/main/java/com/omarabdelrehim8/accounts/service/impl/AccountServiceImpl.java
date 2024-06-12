package com.omarabdelrehim8.accounts.service.impl;

import com.omarabdelrehim8.accounts.constants.AccountConstants;
import com.omarabdelrehim8.accounts.dto.*;
import com.omarabdelrehim8.accounts.entity.Account;
import com.omarabdelrehim8.accounts.entity.Customer;
import com.omarabdelrehim8.accounts.exception.CustomerAlreadyExistsException;
import com.omarabdelrehim8.accounts.exception.ResourceNotFoundException;
import com.omarabdelrehim8.accounts.mapper.AccountMapper;
import com.omarabdelrehim8.accounts.mapper.CustomerMapper;
import com.omarabdelrehim8.accounts.repository.AccountRepository;
import com.omarabdelrehim8.accounts.repository.CustomerRepository;
import com.omarabdelrehim8.accounts.service.AccountService;
import com.omarabdelrehim8.accounts.service.client.CardsFeignClient;
import com.omarabdelrehim8.accounts.service.messagebroker.BrokerCommunication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CardsFeignClient cardsFeignClient;
    private final BrokerCommunication brokerCommunication;

    @Override
    public AccountCreationResponseDto createAccountForNewCustomer(CustomerDto customerDto) {

        Customer newCustomer = CustomerMapper.mapToCustomer(customerDto, new Customer());

        // check if customer is already registered
        Optional<Customer> customer = customerRepository.findByMobileNumberOrEmail(newCustomer.getMobileNumber(), newCustomer.getEmail());

        if (customer.isPresent()) {
            // check if both mobile number and email are duplicates
            if ((customer.get().getMobileNumber().equals(newCustomer.getMobileNumber())) && (customer.get().getEmail().equals(newCustomer.getEmail()))) {
                throw new CustomerAlreadyExistsException("Customer is already registered with the given mobile number and email. " +
                        "Please change them and try again");
            }

            // check if only mobile number is a duplicate
            if (customer.get().getMobileNumber().equals(newCustomer.getMobileNumber())) {
                throw new CustomerAlreadyExistsException("Customer is already registered with the given mobile number. " +
                        "Please change it and try again");
            }

            // check if only email is a duplicate
            if (customer.get().getEmail().equals(newCustomer.getEmail())) {
                throw new CustomerAlreadyExistsException("Customer is already registered with the given email. " +
                        "Please change it and try again");
            }
        }

        // create new account and set new customer inside it
        Account newAccount = createAccountInstance();
        newAccount.setCustomer(newCustomer);

        // set new account inside new customer
        newCustomer.setAccounts(List.of(newAccount));

        // save customer and through @OneToMany many relationship also save the account
        Customer savedCustomer = customerRepository.saveAndFlush(newCustomer);

        // send message to message broker
        brokerCommunication.sendCommunication(savedCustomer.getAccounts().get(0), savedCustomer);

        return AccountCreationResponseDto.builder()
                .customerId(savedCustomer.getId())
                .accountNumber(newAccount.getAccountNumber())
                .accountType(newAccount.getAccountType())
                .branchAddress(newAccount.getBranchAddress()).build();
    }

    @Override
    public AccountCreationResponseDto addAccountForExistingCustomer(CustomerDto customerDto) {

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());

        Customer existingCustomer = customerRepository.findByNameAndMobileNumberAndEmail(customer.getName(), customer.getMobileNumber(), customer.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "name, mobile number and email"));

        Account newAccount = createAccountInstance();
        newAccount.setCustomer(existingCustomer);
        accountRepository.save(newAccount);

        brokerCommunication.sendCommunication(newAccount, existingCustomer);

        return AccountCreationResponseDto.builder()
                .customerId(existingCustomer.getId())
                .accountNumber(newAccount.getAccountNumber())
                .accountType(newAccount.getAccountType())
                .branchAddress(newAccount.getBranchAddress()).build();
    }

    private Account createAccountInstance() {

        Account newAccount = new Account();
        long randomAccountNumber = 1000000000L + new Random().nextInt(900000000); // Generates a 10-digit random account number

        newAccount.setAccountNumber(randomAccountNumber);
        newAccount.setAccountType(AccountConstants.SAVINGS);
        newAccount.setBranchAddress(AccountConstants.ADDRESS);

        return newAccount;
    }

    @Override
    public List<AccountDto> fetchAccountsDetails(Long customerId) {

        List<AccountDto> accountsDtoList = new ArrayList<>();

        // get all accounts related to a customer given their mobileNumber
        Optional<List<Account>> accounts = accountRepository.findByCustomerId(customerId);

        if (accounts.isEmpty() || accounts.get().isEmpty()) {
            throw new ResourceNotFoundException("Customer", "customer id");
        }

        // Dto mapping
        for (Account account : accounts.get()) {
            accountsDtoList.add(AccountMapper.mapToAccountDto(account, new AccountDto()));
        }

        return accountsDtoList;
    }

    @Override
    public boolean deleteAccount(Long accountNumber) {

        boolean isDeleted = false;

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "account number"));

        Long customerId = account.getCustomer().getId();

        // if customer still have more than one account, only delete the account
        if (accountRepository.countAccountsByCustomerId(customerId) > 1) {
            accountRepository.delete(account);
        // customer wants to delete last account so delete both account and customer
        } else {
            accountRepository.delete(account);
            customerRepository.deleteById(customerId);
        }

        return isDeleted = true;
    }

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber) {

        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobile number"));

        List<Account> accountsList = accountRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Accounts", "customer id"));

        ResponseEntity<List<CardDto>> cardsList = cardsFeignClient.fetchCardsDetails(customer.getId());

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccounts(accountsList.stream()
                                                    .map(account -> AccountMapper.mapToAccountDto(account, new AccountDto()))
                                                    .toList());
        if (cardsList != null) {
            customerDetailsDto.setCards(cardsList.getBody());
        }

        return customerDetailsDto;
    }

    @Override
    public boolean updateCustomerDetails(CustomerDto customerDto) {
        boolean isUpdated = false;

        Customer customer = customerRepository.findById(customerDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "customer id"));

        customer.setEmail(customerDto.getEmail());
        customer.setMobileNumber(customerDto.getMobileNumber());

        customerRepository.save(customer);

        return isUpdated = true;
    }

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated = false;

        if (accountNumber != null) {
            Account account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Account", "account number"));

            account.setCommunicationSw(true);
            accountRepository.save(account);

            isUpdated = true;
        }

        return isUpdated;
    }

}
