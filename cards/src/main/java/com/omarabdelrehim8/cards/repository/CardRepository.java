package com.omarabdelrehim8.cards.repository;

import com.omarabdelrehim8.cards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<List<Card>> findAllByCustomerId(Long customerId);

    Optional<List<Card>> findAllByAccountNumber(Long accountNumber);

    int countByAccountNumberAndCardType(Long accountNumber, String cardType);

    Optional<Card> findByCardNumber(String cardNumber);
}
