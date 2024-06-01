package com.omarabdelrehim8.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarabdelrehim8.accounts.dto.AccountCreationResponseDto;
import com.omarabdelrehim8.accounts.dto.AccountDto;
import com.omarabdelrehim8.accounts.dto.CustomerDto;
import com.omarabdelrehim8.accounts.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(SpringExtension.class)
public class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    CustomerDto customerDto;
    AccountDto accountDto;
    AccountCreationResponseDto creationResponse;

    @BeforeEach
    void init() {
        customerDto = new CustomerDto();
        customerDto.setName("Regis Aether");
        customerDto.setEmail("regisaether@gmail.com");
        customerDto.setMobileNumber("1234567891");

        accountDto = new AccountDto();
        accountDto.setAccountNumber(1654798325L);
        accountDto.setAccountType("Savings");
        accountDto.setBranchAddress("123 Main Street, New York");
    }

    @Test
    void Should_Succeed_Creating_Account_When_Input_Is_Valid() throws Exception {
        when(accountService.createAccountForNewCustomer(any(CustomerDto.class))).thenReturn(AccountCreationResponseDto.builder()
                                                                                            .customerId(1L)
                                                                                            .accountNumber(1654798325L)
                                                                                            .accountType("Savings")
                                                                                            .branchAddress("123 Main Street, New York").build());

        ResultActions response = mockMvc.perform(post("/api/accounts/create")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(customerDto)))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("createAccountForNewCustomer"));

        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.statusMessage").value("Account created successfully"))
                .andExpect(jsonPath("$.accountNumber").value(1654798325L));
    }

    @Test
    void Should_Fail_Creating_Account_When_Input_Is_Not_Valid() throws Exception {
        customerDto.setMobileNumber("12345");
        customerDto.setName(null);
        customerDto.setEmail("regisaethergmail.com");

        ResultActions response = mockMvc.perform(post("/api/accounts/create")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(customerDto)))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("createAccountForNewCustomer"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[mobileNumber: Mobile number must be 10 digits] " +
                                "[name: Name is required] " +
                                "[email: Email address should be a valid value]"));
    }

    @Test
    void Should_Succeed_Adding_Account_When_Input_Is_Valid() throws Exception {
        when(accountService.addAccountForExistingCustomer(any(CustomerDto.class))).thenReturn(AccountCreationResponseDto.builder()
                                                                                              .customerId(1L)
                                                                                              .accountNumber(1654798325L)
                                                                                              .accountType("Savings")
                                                                                              .branchAddress("123 Main Street, New York").build());

        ResultActions response = mockMvc.perform(post("/api/accounts/add")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(customerDto)))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("addAccountForExistingCustomer"));

        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.statusMessage").value("Account created successfully"))
                .andExpect(jsonPath("$.accountNumber").value(1654798325L));
    }

    @Test
    void Should_Fail_Adding_Account_When_Input_Is_Not_Valid() throws Exception {
        customerDto.setMobileNumber("");
        customerDto.setName("");
        customerDto.setEmail(null);

        ResultActions response = mockMvc.perform(post("/api/accounts/add")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(customerDto)))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("addAccountForExistingCustomer"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[mobileNumber: Mobile number is required] " +
                                "[name: The length of the customer name should be between 5 and 40] " +
                                "[email: Email is required]"));
    }

    @Test
    void Should_Succeed_Fetching_Account_Details_When_Input_Is_Valid() throws Exception {
        List<AccountDto> accountDtoList = List.of(accountDto);

        when(accountService.fetchAccountsDetails(eq(1L))).thenReturn(accountDtoList);

        ResultActions response = mockMvc.perform(get("/api/accounts/fetch-details")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("customerId", "1"))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("fetchAccountsDetails"));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].accountNumber").value(1654798325L));
    }

    @Test
    void Should_Fail_Fetching_Account_Details_When_Input_Is_Not_Valid() throws Exception {
        ResultActions response = mockMvc.perform(get("/api/accounts/fetch-details", 0)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("customerId", "0"))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("fetchAccountsDetails"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[customerId: Customer ID must be bigger than 0]"));
    }

    @Test
    void Should_Succeed_Deleting_Account() throws Exception {
        when(accountService.deleteAccount(eq(1345687538L))).thenReturn(true);

        ResultActions response = mockMvc.perform(delete("/api/accounts/{accountNumber}/delete", 1345687538)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("deleteAccount"));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("Request processed successfully"));
    }

    @Test
    void Should_Fail_Deleting_Account_When_Account_Service_Fails() throws Exception {
        when(accountService.deleteAccount(anyLong())).thenReturn(false);

        ResultActions response = mockMvc.perform(delete("/api/accounts/{accountNumber}/delete", 1345687538)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(handler().handlerType(AccountController.class))
                        .andExpect(handler().methodName("deleteAccount"));

        response.andExpect(status().isExpectationFailed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiPath").value("/api/accounts/1345687538/delete"))
                .andExpect(jsonPath("$.errorCode").value("EXPECTATION_FAILED"))
                .andExpect(jsonPath("$.errorMessage").value("Delete operation failed. Please try again or contact our customer service"));
    }

    @Test
    void Should_Succeed_Fetching_Customer_Details_When_Input_Data_Is_Valid() throws Exception {
        customerDto.setCustomerId(1L);
        when(accountService.fetchCustomerDetails(eq("1234567891"))).thenReturn(customerDto);

        ResultActions response = mockMvc.perform(get("/api/accounts/customer/fetch-details")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("mobileNumber", "1234567891"))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("fetchCustomerDetails"));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerId").value(1L));
    }

    @Test
    void Should_Fail_Fetching_Customer_Details_When_Input_Data_Is_Not_Valid() throws Exception {
        ResultActions response = mockMvc.perform(get("/api/accounts/customer/fetch-details")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("mobileNumber", "123456789"))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("fetchCustomerDetails"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[mobileNumber: Mobile number must be 10 digits]"));
    }

    @Test
    void Should_Succeed_Updating_Customer_Details_When_Input_Data_Is_Valid() throws Exception {
        customerDto.setCustomerId(1L);
        when(accountService.updateCustomerDetails(any(CustomerDto.class))).thenReturn(true);

        ResultActions response = mockMvc.perform(put("/api/accounts/customer/update-details")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(customerDto)))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("updateCustomerDetails"));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("Request processed successfully"));
    }

    @Test
    void Should_Fail_Updating_Customer_Details_When_Input_Data_Is_Not_Valid() throws Exception {
        customerDto.setCustomerId(null);
        customerDto.setMobileNumber("12345");
        customerDto.setEmail("regisaethergmail.com");

        ResultActions response = mockMvc.perform(put("/api/accounts/customer/update-details")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(customerDto)))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("updateCustomerDetails"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[mobileNumber: Mobile number must be 10 digits] [customerId: Customer ID is required] " +
                                "[email: Email address should be a valid value]"));
    }

    @Test
    void Should_Fail_Updating_Customer_Details_When_Account_Service_Fails() throws Exception {
        customerDto.setCustomerId(1L);
        when(accountService.updateCustomerDetails(any(CustomerDto.class))).thenReturn(false);

        ResultActions response = mockMvc.perform(put("/api/accounts/customer/update-details")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(customerDto)))
                                        .andExpect(handler().handlerType(AccountController.class))
                                        .andExpect(handler().methodName("updateCustomerDetails"));

        response.andExpect(status().isExpectationFailed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiPath").value("/api/accounts/customer/update-details"))
                .andExpect(jsonPath("$.errorCode").value("EXPECTATION_FAILED"))
                .andExpect(jsonPath("$.errorMessage").value("Update operation failed. Please try again or contact our customer service"));
    }
}
