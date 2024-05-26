package com.omarabdelrehim8.cards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CardCreationLimitException extends RuntimeException {

    public CardCreationLimitException(String cardType, int cardLimit) {

        super(String.format("Creation limit reached. You can only have a maximum of %d %ss.", cardLimit, cardType));
    }
}
