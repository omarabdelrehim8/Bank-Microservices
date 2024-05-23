package com.omarabdelrehim8.accounts.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.omarabdelrehim8.accounts.dto.AccountDto;
import com.omarabdelrehim8.accounts.dto.CustomerDto;
import com.omarabdelrehim8.accounts.dto.ErrorResponseDto;
import com.omarabdelrehim8.accounts.dto.ResponseDto;
import com.omarabdelrehim8.accounts.dto.constraints.CustomerValidation;
import com.omarabdelrehim8.accounts.dto.constraints.CustomerView;
import com.omarabdelrehim8.accounts.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
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
@Tag(
        name = "CRUD REST APIs For Accounts Microservice",
        description = "REST APIs to create, update, fetch and delete accounts and customers details"
)
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Create Account",
            description = "REST API to create an account for a new customer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "400",
            description = "HTTP Status BAD REQUEST",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "500",
                    description = "HTTP Status INTERNAL SERVER ERROR",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
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

    @Operation(
            summary = "Add Account",
            description = "REST API to add an additional account to an already existing customer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "400",
                    description = "HTTP Status BAD REQUEST",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "HTTP Status NOT FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "HTTP Status INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
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

    @Operation(
            summary = "Fetch Accounts",
            description = "REST API to fetch all the accounts associated to a customer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "HTTP Status OK"),
            @ApiResponse(responseCode = "400",
                    description = "HTTP Status BAD REQUEST",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "HTTP Status NOT FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "HTTP Status INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
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

    @Operation(
            summary = "Delete Account",
            description = "REST API to delete a customer's account. If the account is the last account associated to the customer, the customer's details will also be deleted."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "HTTP Status OK",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "417",
                    description = "HTTP Status EXPECTATION FAILED",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "HTTP Status NOT FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "HTTP Status INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
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

    @Operation(
            summary = "Fetch Customer Details",
            description = "REST API to fetch a customer's details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "HTTP Status OK"),
            @ApiResponse(responseCode = "400",
                    description = "HTTP Status BAD REQUEST",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "HTTP Status NOT FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "HTTP Status INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })
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

    @Operation(
            summary = "Update Customer Details",
            description = "REST API to update a customer's details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "HTTP Status OK",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "417",
                    description = "HTTP Status EXPECTATION FAILED",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "HTTP Status NOT FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )),
            @ApiResponse(responseCode = "400",
                    description = "HTTP Status BAD REQUEST",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "HTTP Status INTERNAL SERVER ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    ))
    })

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
}
