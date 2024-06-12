package com.omarabdelrehim8.accounts.dto;

public record AccountsMsgDto(
        Long accountNumber,
        String name,
        String email,
        String mobileNumber) {
}
