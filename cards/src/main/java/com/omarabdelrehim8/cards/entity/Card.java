package com.omarabdelrehim8.cards.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Card extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    private Long customerId;

    private Long accountNumber;

    private String cardNumber;

    private String cardType;

    private Integer monthlyPurchaseLimit;

    private BigDecimal amountUsed;

    private BigDecimal currentAvailableAmount;
}
