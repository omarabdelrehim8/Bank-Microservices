package com.omarabdelrehim8.accounts.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.omarabdelrehim8.accounts.dto.*;
import com.omarabdelrehim8.accounts.dto.constraints.CustomerValidation;
import com.omarabdelrehim8.accounts.dto.constraints.CustomerView;
import com.omarabdelrehim8.accounts.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.omarabdelrehim8.accounts.constants.AccountConstants.*;

@RestController
@RequestMapping(value = "/api/accounts", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private AccountsContactInfoDto accountsContactInfoDto;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccountForNewCustomer (@Validated(CustomerValidation.OnCreate.class)
                                                                    @RequestBody
                                                                    @JsonView(CustomerView.OnCreate.class)
                                                                    CustomerDto customerDto) {

        accountService.createAccountForNewCustomer(customerDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.value(), MESSAGE_201));
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseDto> addAccountForExistingCustomer (@Validated(CustomerValidation.OnCreate.class)
                                                                      @RequestBody
                                                                      @JsonView(CustomerView.OnCreate.class)
                                                                      CustomerDto customerDto) {

        accountService.addAccountForExistingCustomer(customerDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.value(), MESSAGE_201));
    }

    @GetMapping("/fetch-details")
    public ResponseEntity<List<AccountDto>> fetchAccountsDetails (
            @RequestParam
            @NotBlank(message = "Mobile number is required")
            @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
            String mobileNumber) {

        List<AccountDto> accountsDtoList = accountService.fetchAccountsDetails(mobileNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsDtoList);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount (HttpServletRequest request, @RequestParam Long accountNumber) {
        boolean isDeleted = accountService.deleteAccount(accountNumber);

        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(HttpStatus.OK.value(), MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ErrorResponseDto(request.getRequestURI(),
                            HttpStatus.EXPECTATION_FAILED,
                            MESSAGE_417_DELETE,
                            LocalDateTime.now()));
        }
    }

    @GetMapping("/customer/fetch-details")
    public ResponseEntity<CustomerDto> fetchCustomerDetails (
            @RequestParam
            @NotBlank(message = "Mobile number is required")
            @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
            String mobileNumber) {

        CustomerDto customerDto = accountService.fetchCustomerDetails(mobileNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerDto);
    }

    @PutMapping("/customer/update-details")
    public ResponseEntity<?> updateCustomerDetails (HttpServletRequest request, @Validated(CustomerValidation.OnUpdate.class)
                                                                                @RequestBody
                                                                                @JsonView(CustomerView.OnUpdate.class)
                                                                                CustomerDto customerDto) {

        boolean isUpdated = accountService.updateCustomerDetails(customerDto);

        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(HttpStatus.OK.value(), MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ErrorResponseDto(request.getRequestURI(),
                                               HttpStatus.EXPECTATION_FAILED,
                                               MESSAGE_417_UPDATE,
                                               LocalDateTime.now()));
        }
    }

    @GetMapping("/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(buildVersion);
    }

    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }

    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactInfoDto);
    }

}
