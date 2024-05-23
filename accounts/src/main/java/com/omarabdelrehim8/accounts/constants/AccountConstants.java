package com.omarabdelrehim8.accounts.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // prevents object instantiation from this class
public class AccountConstants {

    public static final String SAVINGS = "Savings";
    public static final String ADDRESS = "123 Main Street, New York";
    public static final String MESSAGE_201 = "Account created successfully";
    public static final String MESSAGE_200 = "Request processed successfully";
    public static final String MESSAGE_417_UPDATE = "Update operation failed. Please try again or contact our customer service";
    public static final String MESSAGE_417_DELETE = "Delete operation failed. Please try again or contact our customer service";
}
