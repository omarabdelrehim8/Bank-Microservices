package com.omarabdelrehim8.cards.service;

import com.omarabdelrehim8.cards.dto.CardDto;

import java.util.List;

public interface CardService {

    /**
     * Create Debit Card
     *
     * @param customerId
     */
    CardDto createDebitCard(Long customerId, Long accountNumber);

    /**
     * Create Credit Card
     *
     * @param customerId
     */
    CardDto createCreditCard(Long customerId, Long accountNumber);

    /**
     * Fetch Cards Details
     *
     * @param customerId
     *  @return Cards Details based on a given customer id
     */
    List<CardDto> fetchCards(Long customerId);

    /**
     * Update Card Credit Limit
     *
     * @param cardNumber - Number of the selected card
     * @param monthlyPurchaseLimit -  Card's new total limit
     * @return boolean indicating if the update of card limit was successful or not
     */
    boolean updateCardMonthlyPurchaseLimit(String cardNumber, Integer monthlyPurchaseLimit);

    /**
     * Delete Card
     *
     * @param cardNumber - Input Card Number
     * @return boolean indicating if the deletion of card details was successful or not
     */
    boolean deleteCard(String cardNumber);

    /**
     * Delete Cards By Account Number
     *
     * @param accountNumber - Input accountNumber
     * @return boolean indicating if the deletion of card details was successful or not
     */
    boolean deleteCardsByAccountNumber(Long accountNumber);

    /**
     * Delete All Cards
     * @param customerId
     * @return boolean indicating if the deletion of all cards details was successful or not
     */
    boolean deleteAllCards(Long customerId);
}
