package com.omarabdelrehim8.cards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AccountNumberAndCustomerIdMismatchException extends RuntimeException {

    public AccountNumberAndCustomerIdMismatchException(Long accountNumber) {
        super(String.format("Account number: %d is not associated to the given customer id. Please give a valid account number and try again.", accountNumber));
    }
}
