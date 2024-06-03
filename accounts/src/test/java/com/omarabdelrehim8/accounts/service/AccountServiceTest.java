package com.omarabdelrehim8.accounts.service;

import com.omarabdelrehim8.accounts.dto.AccountCreationResponseDto;
import com.omarabdelrehim8.accounts.dto.CardDto;
import com.omarabdelrehim8.accounts.dto.CustomerDetailsDto;
import com.omarabdelrehim8.accounts.dto.CustomerDto;
import com.omarabdelrehim8.accounts.entity.Account;
import com.omarabdelrehim8.accounts.entity.Customer;
import com.omarabdelrehim8.accounts.exception.CustomerAlreadyExistsException;
import com.omarabdelrehim8.accounts.exception.ResourceNotFoundException;
import com.omarabdelrehim8.accounts.repository.AccountRepository;
import com.omarabdelrehim8.accounts.repository.CustomerRepository;
import com.omarabdelrehim8.accounts.service.client.CardsFeignClient;
import com.omarabdelrehim8.accounts.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardsFeignClient cardsFeignClient;

    @InjectMocks
    private AccountServiceImpl accountService;

    private CustomerDto customerDto;
    private Customer customer;
    private AccountCreationResponseDto response;

    @BeforeEach
    void init() {
        customerDto = new CustomerDto();
        customerDto.setName("Regis Aether");
        customerDto.setEmail("regisaether@gmail.com");
        customerDto.setMobileNumber("1234567891");

        customer = new Customer(1L, "Regis Aether", "regisaether@gmail.com", "1234567891");
    }

    @Test
    void Should_Create_An_Account_Instance() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method methodCall = accountService.getClass().getDeclaredMethod("createAccountInstance");
        methodCall.setAccessible(true);

        Account expectedAccount = (Account) methodCall.invoke(accountService);

        assertThat(expectedAccount).isInstanceOf(Account.class);
        assertThat(expectedAccount.getAccountNumber()).isNotZero();
        assertThat(expectedAccount.getAccountType()).isEqualTo("Savings");
        assertThat(expectedAccount.getBranchAddress()).isEqualTo("123 Main Street, New York");
    }

    @Test
    void Create_New_Account_Should_Create_New_Account_For_Given_Customer() {

        when(customerRepository.findByMobileNumberOrEmail(eq("1234567891"), eq("regisaether@gmail.com")))
                .thenReturn(Optional.empty());
        when(customerRepository.saveAndFlush(any(Customer.class))).thenReturn(customer);

        response = accountService.createAccountForNewCustomer(customerDto);

        assertThat(response).extracting("customerId", "accountType").containsExactly(1L, "Savings");
    }

    @Test
    void Create_New_Account_Should_Throw_Customer_Already_Registered_With_Inputted_Mobile_Number_And_Email(){
        when(customerRepository.findByMobileNumberOrEmail(eq("1234567891"), eq("regisaether@gmail.com")))
                .thenReturn(Optional.ofNullable(customer));

        assertThatThrownBy(() -> accountService.createAccountForNewCustomer(customerDto))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessage("Customer is already registered with the given mobile number and email. " +
                        "Please change them and try again");

    }

    @Test
    void Create_New_Account_Should_Throw_Customer_Already_Registered_With_Inputted_Email(){
        customerDto.setMobileNumber("1234567892");

        when(customerRepository.findByMobileNumberOrEmail(eq("1234567892"), eq("regisaether@gmail.com")))
                .thenReturn(Optional.ofNullable(customer));

        assertThatThrownBy(() -> accountService.createAccountForNewCustomer(customerDto))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessage("Customer is already registered with the given email. " +
                        "Please change it and try again");

    }

    @Test
    void Create_New_Account_Should_Throw_Customer_Already_Registered_With_Inputted_Mobile_Number(){
        customerDto.setEmail("sylvieidrath@gmail.com");

        when(customerRepository.findByMobileNumberOrEmail(eq("1234567891"), eq("sylvieidrath@gmail.com")))
                .thenReturn(Optional.ofNullable(customer));

        assertThatThrownBy(() -> accountService.createAccountForNewCustomer(customerDto))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessage("Customer is already registered with the given mobile number. " +
                        "Please change it and try again");

    }

    @Test
    void Should_Add_New_Account_For_Given_Customer() {

        when(customerRepository.findByNameAndMobileNumberAndEmail(eq("Regis Aether"), eq("1234567891"), eq("regisaether@gmail.com")))
                .thenReturn(Optional.ofNullable(customer));

        response = accountService.addAccountForExistingCustomer(customerDto);

        verify(accountRepository, times(1)).save(any(Account.class));
        assertThat(response).extracting("customerId", "accountType").containsExactly(1L, "Savings");
    }

    @Test
    void Add_Account_Should_Throw_Resources_Not_Found_Exception() {
        assertThatThrownBy(() -> accountService.addAccountForExistingCustomer(customerDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer not found with the given name, mobile number and email");
    }

    @Test
    void Should_Return_Accounts_List_For_Given_Mobile_Number() {
        List<Account> accountsList = List.of(new Account());

        when(accountRepository.findByCustomerId(anyLong()))
                .thenReturn(Optional.of(accountsList));

        assertThat(accountService.fetchAccountsDetails(anyLong()))
                .isInstanceOf(List.class);
    }

    @Test
    void Fetching_Accounts_Should_Throw_Resources_Not_Found_Exception() {
        assertThatThrownBy(() -> accountService.fetchAccountsDetails(anyLong()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer not found with the given customer id");
    }

    @Test
    void Should_Return_True_After_Only_Deleting_Account() {
        Account testAccount = new Account();
        testAccount.setCustomer(customer);

        when(accountRepository.findByAccountNumber(anyLong()))
                .thenReturn(Optional.of(testAccount));

        when(accountRepository.countAccountsByCustomerId(customer.getId()))
                .thenReturn(2);

        assertThat(accountService.deleteAccount(anyLong())).isTrue();

        verify(accountRepository, times(1)).delete(testAccount);
        verify(customerRepository, never()).deleteById(customer.getId());
    }

    @Test
    void Should_Return_True_After_Deleting_Both_Account_And_Customer() {
        Account testAccount = new Account();
        testAccount.setCustomer(customer);

        when(accountRepository.findByAccountNumber(anyLong()))
                .thenReturn(Optional.of(testAccount));

        when(accountRepository.countAccountsByCustomerId(customer.getId()))
                .thenReturn(1);

        assertThat(accountService.deleteAccount(anyLong())).isEqualTo(true);

        verify(accountRepository, times(1)).delete(testAccount);
        verify(customerRepository, times(1)).deleteById(customer.getId());
    }

    @Test
    void Deleting_Account_Should_Throw_Resources_Not_Found_Exception() {
        assertThatThrownBy(() -> accountService.deleteAccount(anyLong()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Account not found with the given account number");
    }

    @Test
    void Should_Return_Customer_Details() {
        Account account = new Account(customer, 1023546878L, "Savings", "123 Main Street, New York");

        List<Account> accountsList = new ArrayList<>();
        accountsList.add(account);

        List<CardDto> cardsList = new ArrayList<>();
        cardsList.add(CardDto.builder()
                             .cardNumber("102345678212")
                             .accountNumber(1023546878L)
                             .cardType("Debit Card")
                             .monthlyPurchaseLimit(5000)
                             .amountUsed(BigDecimal.valueOf(0))
                             .currentAvailableAmount(BigDecimal.valueOf(5000)).build());

        when(customerRepository.findByMobileNumber(customer.getMobileNumber()))
                .thenReturn(Optional.ofNullable(customer));

        when(accountRepository.findByCustomerId(anyLong()))
                .thenReturn(Optional.of(accountsList));

        when (cardsFeignClient.fetchCardsDetails(anyLong())).thenReturn(ResponseEntity.of(Optional.of(cardsList)));

        CustomerDetailsDto customerDetailsDto = accountService.fetchCustomerDetails(customer.getMobileNumber());

        assertThat(customerDetailsDto).isInstanceOf(CustomerDetailsDto.class)
                                      .extracting("mobileNumber")
                                      .isEqualTo("1234567891");

        assertThat(customerDetailsDto.getAccounts().get(0).getAccountNumber()).isEqualTo(1023546878L);
        assertThat(customerDetailsDto.getCards().get(0).getCardNumber()).isEqualTo("102345678212");

    }

    @Test
    void Fetching_Customer_Details_Should_Throw_Resources_Not_Found_Exception() {
        assertThatThrownBy(() -> accountService.fetchCustomerDetails(customer.getMobileNumber()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer not found with the given mobile number");
    }

    @Test
    void Should_Return_True_After_Updating_Customer_Details() {
        when(customerRepository.findById(customerDto.getCustomerId()))
                .thenReturn(Optional.ofNullable(customer));

        assertThat(accountService.updateCustomerDetails(customerDto)).isTrue();

        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void Updating_Customer_Details_Should_Throw_Resources_Not_Found_Exception() {
        assertThatThrownBy(() -> accountService.updateCustomerDetails(customerDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer not found with the given customer id");
    }
}
