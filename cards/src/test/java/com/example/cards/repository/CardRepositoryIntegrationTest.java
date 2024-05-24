package com.example.cards.repository;

import com.example.cards.audit.AuditorProvider;
import com.example.cards.entity.Card;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Import(AuditorProvider.class)
public class CardRepositoryIntegrationTest {

    @Autowired
    private CardRepository cardRepository;

    @Container
    @ServiceConnection
    private static final MySQLContainer mysql = new MySQLContainer<>(DockerImageName.parse("mysql"))
            .withDatabaseName("cardsdb")
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema-test.sql"), "/docker-entrypoint-initdb.d/");

    @Test
    @Order(1)
    void Connection_Established() {
        assertThat(mysql.isCreated()).isTrue();
        assertThat(mysql.isRunning()).isTrue();
    }

    @BeforeTransaction
    void init() {
        List<Card> cards = List.of(new Card(1L, 16L, "112345678911", "Debit Card", 5000, BigDecimal.valueOf(0), BigDecimal.valueOf(5000)),
                new Card(2L, 16L, "112345678912", "Debit Card", 5000, BigDecimal.valueOf(0), BigDecimal.valueOf(5000)),
                new Card(3L, 16L, "112345678913", "Credit Card", 5000, BigDecimal.valueOf(0), BigDecimal.valueOf(5000)));

        cardRepository.saveAll(cards);
    }

    @Test
    void Should_Return_All_Cards_Of_A_Customer() {
        List<Card> cardsList = cardRepository.findAllByCustomerId(16L).get();

        assertThat(cardsList).size().isEqualTo(3);
        assertThat(cardsList).extracting(Card::getCardId).isEqualTo(List.of(1L, 2L, 3L));
    }

    @Test
    void Should_Return_Count_Of_Cards() {
        int count = cardRepository.countByCustomerIdAndCardType(16L, "Debit Card");

        assertThat(count).isEqualTo(2);
    }

    @Test
    void Should_Return_Who_Inserted_The_Customer_Details() {
        String auditor = cardRepository.findById(1L).get().getCreatedBy();

        assertThat(auditor).isEqualTo("CARDS_MS");
    }

    @Test
    void Should_Return_Customer_Details_Creation_Date() {
        LocalDate date = cardRepository.findById(1L).get().getCreatedAt().toLocalDate();

        assertThat(date).isEqualTo(LocalDate.now());
    }
    @Test
    void Should_Return_Who_Updated_Customer_Details() {
        Card card = cardRepository.findById(1L).get();
        card.setMonthlyPurchaseLimit(2000);
        cardRepository.saveAndFlush(card);

        assertThat(card.getUpdatedBy()).isEqualTo("CARDS_MS");
    }

    @Test
    void Should_Return_When_Customer_Details_Were_Updated() {
        Card card = cardRepository.findById(1L).get();
        card.setMonthlyPurchaseLimit(2000);
        cardRepository.saveAndFlush(card);

        LocalDate date = card.getUpdatedAt().toLocalDate();

        assertThat(date).isEqualTo(LocalDate.now());
    }
}
