package com.example.cards.service.Impl;

import com.example.cards.dto.CardDto;
import com.example.cards.entity.Card;
import com.example.cards.exception.CardCreationLimitException;
import com.example.cards.exception.CustomResourceNotFoundException;
import com.example.cards.mapper.CardMapper;
import com.example.cards.repository.CardRepository;
import com.example.cards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.example.cards.constants.CardConstants.*;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public CardDto createDebitCard(Long customerId) {
        CardDto cardDto = new CardDto();

        // Cards count should be less than 2 to create a new Card
        if (cardRepository.countByCustomerIdAndCardType(customerId, DEBIT_CARD) < 2 ) {
            Card card = cardRepository.save(createNewCard(customerId, DEBIT_CARD));
            CardMapper.mapToCardDto(card, cardDto);
        } else {
            throw new CardCreationLimitException(DEBIT_CARD, DEBIT_CARD_CREATION_LIMIT);
        }

        return cardDto;
    }

    @Override
    public CardDto createCreditCard(Long customerId) {
        CardDto cardDto = new CardDto();

        // Cards count should be less than 3 to create a new Card
        if (cardRepository.countByCustomerIdAndCardType(customerId, CREDIT_CARD) < 3) {
            Card card = cardRepository.save(createNewCard(customerId, CREDIT_CARD));
            CardMapper.mapToCardDto(card, cardDto);
        } else {
            throw new CardCreationLimitException(CREDIT_CARD, CREDIT_CARD_CREATION_LIMIT);
        }

        return cardDto;
    }

    private Card createNewCard(Long customerId, String cardType) {
        Card newCard = new Card();

        if (Objects.equals(cardType, DEBIT_CARD)) {
            long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
            newCard.setCardNumber(Long.toString(randomCardNumber));
            newCard.setCustomerId(customerId);
            newCard.setCardType(DEBIT_CARD);
            newCard.setMonthlyPurchaseLimit(NEW_DEBIT_CARD_LIMIT);
            newCard.setAmountUsed(BigDecimal.valueOf(0));
            newCard.setCurrentAvailableAmount(BigDecimal.valueOf(NEW_DEBIT_CARD_LIMIT));
        } else {
            long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
            newCard.setCardNumber(Long.toString(randomCardNumber));
            newCard.setCustomerId(customerId);
            newCard.setCardType(CREDIT_CARD);
            newCard.setMonthlyPurchaseLimit(NEW_CREDIT_CARD_LIMIT);
            newCard.setAmountUsed(BigDecimal.valueOf(0));
            newCard.setCurrentAvailableAmount(BigDecimal.valueOf(NEW_CREDIT_CARD_LIMIT));
        }

        return newCard;
    }

    @Override
    public List<CardDto> fetchCards(Long customerId) {
        Optional<List<Card>> cardsList = cardRepository.findAllByCustomerId(customerId);

        if (cardsList.isEmpty() || cardsList.get().isEmpty()) {
            throw new CustomResourceNotFoundException("Cards", "customer id");
        }

        List<CardDto> cardsDtoList = new ArrayList<>();

        for (Card card : cardsList.get()) {
            cardsDtoList.add(CardMapper.mapToCardDto(card, new CardDto()));
        }
        return cardsDtoList;
    }

    @Override
    public boolean updateCardMonthlyPurchaseLimit(String cardNumber, Integer newPurchaseLimit) {
        Card card = cardRepository.findByCardNumber(cardNumber).orElseThrow(
                () -> new CustomResourceNotFoundException("Card", "card number"));

        card.setMonthlyPurchaseLimit(newPurchaseLimit);
        cardRepository.save(card);

        return true;
    }

    @Override
    public boolean deleteCard(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber).orElseThrow(
                () -> new CustomResourceNotFoundException("Card", "card number")
        );
        cardRepository.deleteById(card.getCardId());
        return true;
    }

    @Override
    public boolean deleteAllCards(Long customerId) {
        Optional<List<Card>> cardsList = cardRepository.findAllByCustomerId(customerId);

        if (cardsList.isEmpty() || cardsList.get().isEmpty()) {
            throw new CustomResourceNotFoundException("Cards", "customer id");
        }

        cardRepository.deleteAll(cardsList.get());

        return true;
    }
}
