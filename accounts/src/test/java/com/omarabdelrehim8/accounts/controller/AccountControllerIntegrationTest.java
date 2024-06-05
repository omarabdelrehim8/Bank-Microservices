package com.omarabdelrehim8.accounts.controller;

import com.omarabdelrehim8.accounts.dto.*;
import com.omarabdelrehim8.accounts.repository.AccountRepository;
import com.omarabdelrehim8.accounts.repository.CustomerRepository;
import com.omarabdelrehim8.accounts.service.AccountService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountControllerIntegrationTest {

    @Container
    @ServiceConnection
    private static final MySQLContainer mysql = new MySQLContainer<>(DockerImageName.parse("mysql"))
            .withDatabaseName("accountsdb")
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema.sql"), "/docker-entrypoint-initdb.d/");

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    ApplicationContext context;

    @Autowired
    TestRestTemplate testRestTemplate;

    @LocalServerPort
    int port;

    CustomerDto customerDto;
    AccountDto accountDto;
    AccountCreationResponseDto accountCreationResponseDto;

    @BeforeEach
    void init() {
        customerDto = new CustomerDto();
        customerDto.setName("Regis Aether");
        customerDto.setEmail("regisaether@gmail.com");
        customerDto.setMobileNumber("1234567893");

        accountCreationResponseDto = accountService.createAccountForNewCustomer(customerDto);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @Order(1)
    void Connection_Established() {
        assertThat(mysql.isCreated()).isTrue();
        assertThat(mysql.isRunning()).isTrue();
        assertThat(context.containsBean("auditorProvider")).isTrue();
    }

    @Test
    void Should_Succeed_Creating_Account() {
        customerDto.setName("Sylvie Idrath");
        customerDto.setEmail("sylvieidrath@gmail.com");
        customerDto.setMobileNumber("1234567892");

        ResponseEntity<AccountCreationResponseDto> response = testRestTemplate.postForEntity("/api/create", customerDto, AccountCreationResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatusCode()).isEqualTo(201);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Account created successfully");
        assertThat(response.getBody().getAccountType()).isEqualTo("Savings");
    }

    @Test
    void Should_Fail_Creating_Account() {
        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity("/api/create", customerDto, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/create");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Customer is already registered with the given mobile number and email. " +
                "Please change them and try again");
    }

    @Test
    void Should_Succeed_Adding_Account() {
        ResponseEntity<AccountCreationResponseDto> response = testRestTemplate.postForEntity("/api/add", customerDto, AccountCreationResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatusCode()).isEqualTo(201);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Account created successfully");
        assertThat(response.getBody().getAccountType()).isEqualTo("Savings");
    }

    @Test
    void Should_Fail_Adding_Account() {
        customerDto.setMobileNumber("1234567891");
        customerDto.setName("Arthur Leywin");
        customerDto.setEmail("arthurleywin@gmail.com");

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity("/api/add", customerDto, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/add");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Customer not found with the given name, mobile number and email");
    }

    @Test
    void Should_Succeed_Fetching_Account_Details() {
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/fetch-details")
                .queryParam("customerId", accountCreationResponseDto.getCustomerId()).build().toUri();

        ResponseEntity<List<AccountDto>> response = testRestTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<AccountDto>>(){});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get(0).getAccountNumber()).isNotZero();
        assertThat(response.getBody().get(0).getAccountType()).isEqualTo("Savings");
    }

    @Test
    void Should_Fail_Fetching_Account_Details() {
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/fetch-details")
                .queryParam("customerId", "10").build().toUri();

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.GET, null, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/fetch-details");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Customer not found with the given customer id");
    }

    @Test
    @Disabled("Needs feign client mock")
    void Should_Succeed_Fetching_Customer_Details() {
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/customer/fetch-details")
                .queryParam("mobileNumber", "1234567893").build().toUri();

        ResponseEntity<CustomerDetailsDto> response = testRestTemplate.exchange(uri, HttpMethod.GET, null, CustomerDetailsDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getEmail()).isEqualTo("regisaether@gmail.com");
        assertThat(response.getBody().getName()).isEqualTo("Regis Aether");
    }

    @Test
    void Should_Fail_Fetching_Customer_Details() {
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/customer/fetch-details")
                .queryParam("mobileNumber", "1234567892").build().toUri();

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.GET, null, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/customer/fetch-details");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Customer not found with the given mobile number");
    }

    @Test
    void Should_Succeed_Updating_Customer_Details() {
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/customer/update-details").build().toUri();

        customerDto.setCustomerId(customerRepository.findByMobileNumber(customerDto.getMobileNumber()).get().getId());
        customerDto.setMobileNumber("1234567894");

        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(customerDto), ResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Request processed successfully");
    }

    @Test
    void Should_Fail_Updating_Customer_Details_If_Customer_Does_Not_Exist() {
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/customer/update-details").build().toUri();

        customerDto.setCustomerId(2L);

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(customerDto), ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/customer/update-details");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Customer not found with the given customer id");
    }

    @Test
    void Should_Succeed_Deleting_Account_And_Customer() {
        Long accountNumber = accountCreationResponseDto.getAccountNumber();

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("accountNumber", accountNumber.toString());
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                                      .path("/api/{accountNumber}/delete")
                                      .buildAndExpand(uriVariables)
                                      .toUri();

        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.DELETE, null, ResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Request processed successfully");
        // Customer's details should also be deleted when their last account is deleted, here, we're asserting that
        assertThat(customerRepository.findByMobileNumber(customerDto.getMobileNumber()).isEmpty()).isTrue();
    }

    @Test
    void Should_Succeed_Deleting_Account_Only() {
        Long accountNumber = accountCreationResponseDto.getAccountNumber();

        accountService.addAccountForExistingCustomer(customerDto);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("accountNumber", accountNumber.toString());
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/api/{accountNumber}/delete")
                .buildAndExpand(uriVariables)
                .toUri();

        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.DELETE, null, ResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Request processed successfully");
        // Customer's details should also be deleted when their last account is deleted, here, we're asserting that
        assertThat(customerRepository.findByMobileNumber(customerDto.getMobileNumber()).isEmpty()).isFalse();
    }

    @Test
    void Should_Fail_Deleting_Account() {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("accountNumber", "1111111111");
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/api/{accountNumber}/delete")
                .buildAndExpand(uriVariables)
                .toUri();

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.DELETE, null,  ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/1111111111/delete");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Account not found with the given account number");
    }
}