package com.omarabdelrehim8.cards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarabdelrehim8.cards.dto.CardCreationRequestDto;
import com.omarabdelrehim8.cards.dto.CardDto;
import com.omarabdelrehim8.cards.entity.Card;
import com.omarabdelrehim8.cards.mapper.CardMapper;
import com.omarabdelrehim8.cards.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(SpringExtension.class)
public class CardControllerTest {

    @MockBean
    private CardService cardService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    Card card;
    CardCreationRequestDto creationRequest;

    @BeforeEach
    void init() {
        card = new Card(1L,
                "102345678911",
                16L,
                1023456871L,
                "Debit Card",
                5000,
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(5000));
    }

    @Test
    void Should_Succeed_Creating_A_Debit_Card_When_Input_Is_Valid() throws Exception {
        CardDto cardDto = CardMapper.mapToCardDto(card, new CardDto());
        creationRequest = CardCreationRequestDto.builder()
                                                .customerId(card.getCustomerId())
                                                .accountNumber(card.getAccountNumber().toString())
                                                .build();

        when(cardService.createDebitCard(anyLong(), anyLong())).thenReturn(cardDto);

        ResultActions response = mockMvc.perform(post("/api/create/debit-card")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(creationRequest)))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("createDebitCard"));

        verify(cardService, times(1)).createDebitCard(eq(16L), eq(1023456871L));

        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.statusMessage").value("Card created successfully"))
                .andExpect(jsonPath("$.card.cardNumber").value("102345678911"));
    }

    @Test
    void Should_Fail_Creating_Debit_Card_When_Input_Is_Not_Valid() throws Exception {
        creationRequest = CardCreationRequestDto.builder()
                                                .customerId(0L)
                                                .accountNumber(card.getAccountNumber().toString())
                                                .build();

        ResultActions response = mockMvc.perform(post("/api/create/debit-card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequest)))
                        .andExpect(handler().handlerType(CardController.class))
                        .andExpect(handler().methodName("createDebitCard"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[customerId: Customer ID must be bigger than 0]"));
    }

    @Test
    void Should_Succeed_Creating_A_Credit_Card_When_Input_Is_Valid() throws Exception {
        CardDto cardDto = CardMapper.mapToCardDto(card, new CardDto());
        creationRequest = CardCreationRequestDto.builder()
                                                .customerId(card.getCustomerId())
                                                .accountNumber(card.getAccountNumber().toString())
                                                .build();

        when(cardService.createCreditCard(anyLong(), anyLong())).thenReturn(cardDto);

        ResultActions response = mockMvc.perform(post("/api/create/credit-card")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(creationRequest)))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("createCreditCard"));

        verify(cardService, times(1)).createCreditCard(eq(16L), eq(1023456871L));

        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.statusMessage").value("Card created successfully"))
                .andExpect(jsonPath("$.card.cardNumber").value("102345678911"));
    }

    @Test
    void Should_Fail_Creating_Credit_Card_When_Input_Is_Not_Valid() throws Exception {
        creationRequest = CardCreationRequestDto.builder()
                                                .customerId(0L)
                                                .accountNumber(card.getAccountNumber().toString())
                                                .build();

        ResultActions response = mockMvc.perform(post("/api/create/credit-card")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(creationRequest)))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("createCreditCard"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[customerId: Customer ID must be bigger than 0]"));
    }

    @Test
    void Should_Succeed_Fetching_All_Cards_When_Input_Is_Valid() throws Exception {
        List<CardDto> cardsList = List.of(CardMapper.mapToCardDto(card, new CardDto()));

        when(cardService.fetchCards(eq(16L))).thenReturn(cardsList);

        ResultActions response = mockMvc.perform(get("/api/fetch")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("customerId", "16"))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("fetchCardsDetails"));

        verify(cardService, times(1)).fetchCards(eq(16L));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].customerId").value(16L))
                .andExpect(jsonPath("$[0].cardType").value("Debit Card"));

    }

    @Test
    void Should_Fail_Fetching_All_Cards_When_Input_Is_Not_Valid() throws Exception {
        ResultActions response = mockMvc.perform(get("/api/fetch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("customerId", "0"))
                        .andExpect(handler().handlerType(CardController.class))
                        .andExpect(handler().methodName("fetchCardsDetails"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[customerId: Customer ID must be bigger than 0]"));
    }

    @Test
    void Should_Succeed_Updating_Card_Purchase_Limit_When_Input_Data_Is_Valid() throws Exception {
        CardDto cardDto = CardMapper.mapToCardDto(card, new CardDto());
        when(cardService.updateCardMonthlyPurchaseLimit(anyString(), anyInt())).thenReturn(true);

        ResultActions response = mockMvc.perform(put("/api/{cardNumber}/update", cardDto.getCardNumber())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(cardDto)))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("updateCardPurchaseLimit"));

        verify(cardService, times(1)).updateCardMonthlyPurchaseLimit(anyString(), anyInt());

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("Request processed successfully"));
    }

    @Test
    void Should_Fail_Updating_Card_Purchase_Limit_When_Input_Data_Is_Not_Valid() throws Exception {
        CardDto cardDto = CardMapper.mapToCardDto(card, new CardDto());
        cardDto.setMonthlyPurchaseLimit(1000);
        when(cardService.updateCardMonthlyPurchaseLimit(anyString(), anyInt())).thenReturn(true);

        ResultActions response = mockMvc.perform(put("/api/{cardNumber}/update", cardDto.getCardNumber())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(cardDto)))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("updateCardPurchaseLimit"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[monthlyPurchaseLimit: Monthly purchase limit should be bigger than or equal to 2000]"));
    }

    @Test
    void Should_Fail_Updating_Card_Purchase_Limit_When_Card_Service_Fails() throws Exception {
        CardDto cardDto = CardMapper.mapToCardDto(card, new CardDto());
        when(cardService.updateCardMonthlyPurchaseLimit(anyString(), anyInt())).thenReturn(false);

        ResultActions response = mockMvc.perform(put("/api/{cardNumber}/update", cardDto.getCardNumber())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(cardDto)))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("updateCardPurchaseLimit"));

        response.andExpect(status().isExpectationFailed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("EXPECTATION_FAILED"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("Update operation failed. Please try again or contact our customer service"));
    }

    @Test
    void Should_Succeed_Deleting_Card_When_Input_Data_Is_Valid() throws Exception {
        when(cardService.deleteCard(anyString())).thenReturn(true);

        ResultActions response = mockMvc.perform(delete("/api/{cardNumber}/delete", card.getCardNumber())
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("deleteCard"));

        verify(cardService, times(1)).deleteCard(anyString());

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("Request processed successfully"));
    }

    @Test
    void Should_Fail_Deleting_Card_When_Input_Data_Is_Not_Valid() throws Exception {
        ResultActions response = mockMvc.perform(delete("/api/{cardNumber}/delete", 0)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("deleteCard"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[cardNumber: Card number must be 12 digits]"));
    }

    @Test
    void Should_Fail_Deleting_Card_When_Card_Service_Fails() throws Exception {
        when(cardService.deleteCard(anyString())).thenReturn(false);

        ResultActions response = mockMvc.perform(delete("/api/{cardNumber}/delete", card.getCardNumber())
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("deleteCard"));

        response.andExpect(status().isExpectationFailed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("EXPECTATION_FAILED"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("Delete operation failed. Please try again or contact our customer service"));
    }

    @Test
    void Should_Succeed_Deleting_All_Cards_By_Account_Number_When_Input_Data_Is_Valid() throws Exception {
        when(cardService.deleteCardsByAccountNumber(anyLong())).thenReturn(true);

        ResultActions response = mockMvc.perform(delete("/api/delete")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("accountNumber", "1023456871"))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("deleteCardsByAccountNumber"));

        verify(cardService, times(1)).deleteCardsByAccountNumber(anyLong());

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("Request processed successfully"));
    }

    @Test
    void Should_Fail_Deleting_All_Cards_By_Account_Number_When_Input_Data_Is_Not_Valid() throws Exception {
        ResultActions response = mockMvc.perform(delete("/api/delete")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("accountNumber", "0"))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("deleteCardsByAccountNumber"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[accountNumber: Account Number must be 10 digits]"));
    }

    @Test
    void Should_Fail_Deleting_All_Cards_By_Account_Number_When_Card_Service_Fails() throws Exception {
        when(cardService.deleteCardsByAccountNumber(anyLong())).thenReturn(false);

        ResultActions response = mockMvc.perform(delete("/api/delete")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("accountNumber", "1023456871"))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("deleteCardsByAccountNumber"));

        response.andExpect(status().isExpectationFailed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("EXPECTATION_FAILED"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("Delete operation failed. Please try again or contact our customer service"));
    }

    @Test
    void Should_Succeed_Deleting_All_Cards_When_Input_Data_Is_Valid() throws Exception {
        when(cardService.deleteAllCards(anyLong())).thenReturn(true);

        ResultActions response = mockMvc.perform(delete("/api/delete")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("customerId", "16"))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("deleteAllCards"));

        verify(cardService, times(1)).deleteAllCards(anyLong());

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("Request processed successfully"));
    }

    @Test
    void Should_Fail_Deleting_All_Cards_When_Input_Data_Is_Not_Valid() throws Exception {
        ResultActions response = mockMvc.perform(delete("/api/delete")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("customerId", "0"))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("deleteAllCards"));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("[customerId: Customer ID must be bigger than 0]"));
    }

    @Test
    void Should_Fail_Deleting_All_Cards_When_Card_Service_Fails() throws Exception {
        when(cardService.deleteAllCards(anyLong())).thenReturn(false);

        ResultActions response = mockMvc.perform(delete("/api/delete")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("customerId", "16"))
                                        .andExpect(handler().handlerType(CardController.class))
                                        .andExpect(handler().methodName("deleteAllCards"));

        response.andExpect(status().isExpectationFailed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("EXPECTATION_FAILED"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("Delete operation failed. Please try again or contact our customer service"));
    }

}
