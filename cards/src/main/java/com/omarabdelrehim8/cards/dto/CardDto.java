package com.omarabdelrehim8.cards.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.omarabdelrehim8.cards.dto.constraints.CardValidation;
import com.omarabdelrehim8.cards.dto.constraints.CardView;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardDto {

    @NotNull(message = "Customer ID is required")
    @Min(value = 1, message = "Customer ID must be bigger than 0")
    private Long customerId;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp="(^$|[0-9]{12})",message = "Card number must be 12 digits")
    private String cardNumber;

    private String cardType;

    @JsonView(CardView.OnUpdate.class)
    @Min(value = 2000, message = "Monthly purchase limit should be bigger than or equal to 2000", groups = CardValidation.OnUpdate.class)
    @Max(value = 100000, message = "Monthly purchase limit should be less than or equal to 100000", groups = CardValidation.OnUpdate.class)
    private Integer monthlyPurchaseLimit;

    private BigDecimal amountUsed;

    private BigDecimal currentAvailableAmount;
}
