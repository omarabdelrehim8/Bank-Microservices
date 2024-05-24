package com.example.cards.service;

import com.example.cards.dto.CardDto;
import com.example.cards.entity.Card;
import com.example.cards.exception.CardCreationLimitException;
import com.example.cards.exception.CustomResourceNotFoundException;
import com.example.cards.repository.CardRepository;
import com.example.cards.service.Impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CardServiceTest {

    @Mock
    CardRepository cardRepository;

    @InjectMocks
    CardServiceImpl cardService;

    Card card;

    @BeforeEach
    void init() {
        card = new Card(1L,
                    16L,
                   "102345678911",
                     "Debit Card",
            5000,
                              BigDecimal.valueOf(0),
                              BigDecimal.valueOf(5000));
    }


    @Test
    void Should_Create_A_Card_Instance() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method methodCall = cardService.getClass().getDeclaredMethod("createNewCard", Long.class, String.class);
        methodCall.setAccessible(true);

        Card expectedCard = (Card) methodCall.invoke(cardService, card.getCustomerId(), card.getCardType());

        assertThat(expectedCard).isInstanceOf(Card.class);
        assertThat(expectedCard.getCardType()).isEqualTo("Debit Card");
        assertThat(expectedCard.getMonthlyPurchaseLimit()).isEqualTo(5000);
    }

    @Test
    void Should_Create_A_Debit_Card() {
        when(cardRepository.countByCustomerIdAndCardType(anyLong(), anyString())).thenReturn(1);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        assertThat(cardService.createDebitCard(card.getCustomerId()))
                .isInstanceOf(CardDto.class)
                .extracting("customerId", "cardType")
                .containsExactly(16L, "Debit Card");
    }

    @Test
    void Create_Debit_Card_Should_Throw_Exception_When_Cards_Count_2_Or_Higher() {
        when(cardRepository.countByCustomerIdAndCardType(anyLong(), anyString())).thenReturn(2);

        assertThatThrownBy(() -> cardService.createDebitCard(card.getCustomerId()))
                .isInstanceOf(CardCreationLimitException.class)
                .hasMessage("Creation limit reached. You can only have a maximum of 2 Debit Cards.");
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void Should_Create_A_Credit_Card() {
        card.setCardType("Credit Card");
        when(cardRepository.countByCustomerIdAndCardType(anyLong(), anyString())).thenReturn(2);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        assertThat(cardService.createCreditCard(card.getCustomerId()))
                .isInstanceOf(CardDto.class)
                .extracting("customerId", "cardType")
                .containsExactly(16L, "Credit Card");
    }

    @Test
    void Create_Credit_Card_Should_Throw_Exception_When_Cards_Count_3_Or_Higher() {
        when(cardRepository.countByCustomerIdAndCardType(anyLong(), anyString())).thenReturn(3);

        assertThatThrownBy(() -> cardService.createCreditCard(card.getCustomerId()))
                .isInstanceOf(CardCreationLimitException.class)
                .hasMessage("Creation limit reached. You can only have a maximum of 3 Credit Cards.");
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void Should_Return_Cards_List() {
        List<Card> cardsList = List.of(card);

        when(cardRepository.findAllByCustomerId(anyLong()))
                .thenReturn(Optional.of(cardsList));

        assertThat(cardService.fetchCards(card.getCustomerId()))
                .isInstanceOf(List.class)
                .size()
                .isEqualTo(1);
    }

    @Test
    void Fetching_Cards_Should_Throw_Custom_Resource_Not_Found_Exception() {
        assertThatThrownBy(() -> cardService.fetchCards(anyLong()))
                .isInstanceOf(CustomResourceNotFoundException.class)
                .hasMessage("Cards not found with the given customer id");
    }

    @Test
    void Should_Return_True_After_Updating_Card_Monthly_Purchase_Limit() {
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));

        Boolean result = cardService.updateCardMonthlyPurchaseLimit(card.getCardNumber(), 10000);

        assertThat(result).isTrue();
        assertThat(card.getMonthlyPurchaseLimit()).isEqualTo(10000);
        verify(cardRepository, times(1)).save(eq(card));
    }

    @Test
    void Updating_Card_Purchase_Limit_Should_Throw_Custom_Resource_Not_Found_Exception() {
        assertThatThrownBy(() -> cardService.updateCardMonthlyPurchaseLimit(anyString(), 10000))
                .isInstanceOf(CustomResourceNotFoundException.class)
                .hasMessage("Card not found with the given card number");

        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void Should_Return_True_After_Deleting_Card() {
        when(cardRepository.findByCardNumber(card.getCardNumber())).thenReturn(Optional.of(card));

        Boolean result = cardService.deleteCard(card.getCardNumber());

        assertThat(result).isTrue();
        verify(cardRepository, times(1)).deleteById(eq(card.getCardId()));
    }

    @Test
    void Deleting_Card_Should_Throw_Custom_Resource_Not_Found_Exception() {
        assertThatThrownBy(() -> cardService.deleteCard(anyString()))
                .isInstanceOf(CustomResourceNotFoundException.class)
                .hasMessage("Card not found with the given card number");

        verify(cardRepository, never()).deleteById(anyLong());
    }

    @Test
    void Should_Return_True_After_Deleting_All_Cards() {
        List<Card> cardsList = List.of(card);
        when(cardRepository.findAllByCustomerId(card.getCustomerId())).thenReturn(Optional.of(cardsList));

        Boolean result = cardService.deleteAllCards(card.getCustomerId());

        assertThat(result).isTrue();
        verify(cardRepository, times(1)).deleteAll(eq(cardsList));
    }

    @Test
    void Deleting_All_Cards_Should_Throw_Custom_Resource_Not_Found_Exception() {
        assertThatThrownBy(() -> cardService.deleteAllCards(anyLong()))
                .isInstanceOf(CustomResourceNotFoundException.class)
                .hasMessage("Cards not found with the given customer id");

        verify(cardRepository, never()).deleteAll(anyList());
    }

}
