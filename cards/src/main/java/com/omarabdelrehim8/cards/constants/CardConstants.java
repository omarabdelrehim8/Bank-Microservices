package com.omarabdelrehim8.cards.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // prevents object instantiation from this class
public class CardConstants {

    public static final String  DEBIT_CARD = "Debit Card";
    public static final String  CREDIT_CARD = "Credit Card";
    public static final int  DEBIT_CARD_CREATION_LIMIT = 2;
    public static final int  CREDIT_CARD_CREATION_LIMIT = 3;
    public static final int NEW_DEBIT_CARD_LIMIT = 5_000;
    public static final int NEW_CREDIT_CARD_LIMIT = 20_000;
    public static final String  MESSAGE_201 = "Card created successfully";
    public static final String  MESSAGE_200 = "Request processed successfully";
    public static final String  MESSAGE_417_UPDATE = "Update operation failed. Please try again or contact our customer service";
    public static final String  MESSAGE_417_DELETE = "Delete operation failed. Please try again or contact our customer service";
}
