package com.omarabdelrehim8.accounts.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountDto {

    private Long accountNumber;

    private String accountType;

    private String branchAddress;
}
