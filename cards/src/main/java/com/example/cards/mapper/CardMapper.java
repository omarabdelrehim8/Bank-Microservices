package com.example.cards.mapper;

import com.example.cards.dto.CardDto;
import com.example.cards.entity.Card;

public class CardMapper {

    public static CardDto mapToCardDto(Card card, CardDto cardDto) {
        cardDto.setCardNumber(card.getCardNumber());
        cardDto.setCardType(card.getCardType());
        cardDto.setCustomerId(card.getCustomerId());
        cardDto.setMonthlyPurchaseLimit(card.getMonthlyPurchaseLimit());
        cardDto.setCurrentAvailableAmount(card.getCurrentAvailableAmount());
        cardDto.setAmountUsed(card.getAmountUsed());
        return cardDto;
    }

    public static Card mapToCard(CardDto cardDto, Card card) {
        card.setCardNumber(cardDto.getCardNumber());
        card.setCardType(cardDto.getCardType());
        card.setCustomerId(cardDto.getCustomerId());
        card.setMonthlyPurchaseLimit(cardDto.getMonthlyPurchaseLimit());
        card.setCurrentAvailableAmount(cardDto.getCurrentAvailableAmount());
        card.setAmountUsed(cardDto.getAmountUsed());
        return card;
    }
}
