package com.omarabdelrehim8.accounts.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountCreationResponseDto {

    private int statusCode;

    private String statusMessage;

    private Long customerId;

    private Long accountNumber;

    private String accountType;

    private String branchAddress;
}
