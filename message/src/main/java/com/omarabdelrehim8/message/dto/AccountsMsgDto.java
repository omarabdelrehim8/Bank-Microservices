package com.omarabdelrehim8.message.dto;

public record AccountsMsgDto(
        Long accountNumber,
        String name,
        String email,
        String mobileNumber) {
}
