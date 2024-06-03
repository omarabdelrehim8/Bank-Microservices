package com.omarabdelrehim8.accounts.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDetailsDto {

    private Long customerId;

    private String name;

    private String email;

    private String mobileNumber;

    private List<AccountDto> accounts;

    private List<CardDto> cards;
}
