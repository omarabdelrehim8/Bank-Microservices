package com.omarabdelrehim8.accounts.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CardDto {

    private String cardNumber;

    private Long accountNumber;

    private String cardType;

    private Integer monthlyPurchaseLimit;

    private BigDecimal amountUsed;

    private BigDecimal currentAvailableAmount;
}
