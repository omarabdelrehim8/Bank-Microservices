package com.omarabdelrehim8.cards.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardCreationRequestDto {

    @NotNull(message = "Customer ID is required")
    @Min(value = 1, message = "Customer ID must be bigger than 0")
    private Long customerId;

    @NotNull(message = "Account number is required")
    @Pattern(regexp="(^$|[0-9]{10})",message = "Account Number must be 10 digits")
    private String accountNumber;
}
