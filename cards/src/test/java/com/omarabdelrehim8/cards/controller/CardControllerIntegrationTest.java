package com.omarabdelrehim8.cards.controller;

import com.omarabdelrehim8.cards.dto.CardDto;
import com.omarabdelrehim8.cards.dto.ErrorResponseDto;
import com.omarabdelrehim8.cards.dto.ResponseDto;
import com.omarabdelrehim8.cards.entity.Card;
import com.omarabdelrehim8.cards.repository.CardRepository;
import com.omarabdelrehim8.cards.service.CardService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CardControllerIntegrationTest {

    @Container
    @ServiceConnection
    private static final MySQLContainer mysql = new MySQLContainer<>(DockerImageName.parse("mysql"))
            .withDatabaseName("cardsdb")
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema.sql"), "/docker-entrypoint-initdb.d/");

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardService cardService;

    @Autowired
    ApplicationContext context;

    @Autowired
    TestRestTemplate testRestTemplate;

    @LocalServerPort
    int port;

    Card card;
    CardDto createdCard;

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

    @AfterEach
    void tearDown() {
        cardRepository.deleteAll();
    }

    @Test
    @Order(1)
    void Connection_Established() {
        assertThat(mysql.isCreated()).isTrue();
        assertThat(mysql.isRunning()).isTrue();
        assertThat(context.containsBean("auditorProvider")).isTrue();
    }

    @Test
    void Should_Succeed_Creating_Debit_Card() {
        ResponseEntity<ResponseDto> response = testRestTemplate.postForEntity("/api/cards/{customerId}/create/debit-card", null, ResponseDto.class, card.getCustomerId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatusCode()).isEqualTo(201);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Card created successfully");
        assertThat(response.getBody().getCard()).extracting("customerId", "cardType").containsExactly(16L, "Debit Card");
    }

    @Test
    void Should_Fail_Creating_More_Debit_Cards() {
        testRestTemplate.postForEntity("/api/cards/{customerId}/create/debit-card", null, ResponseDto.class, 16L);
        testRestTemplate.postForEntity("/api/cards/{customerId}/create/debit-card", null, ResponseDto.class, 16L);
        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity("/api/cards/{customerId}/create/debit-card", null, ErrorResponseDto.class, 16L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/cards/16/create/debit-card");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Creation limit reached. You can only have a maximum of 2 Debit Cards.");
    }

    @Test
    void Should_Succeed_Creating_Credit_Card() {
        ResponseEntity<ResponseDto> response = testRestTemplate.postForEntity("/api/cards/{customerId}/create/credit-card", null, ResponseDto.class, card.getCustomerId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatusCode()).isEqualTo(201);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Card created successfully");
        assertThat(response.getBody().getCard()).extracting("customerId", "cardType").containsExactly(16L, "Credit Card");
    }

    @Test
    void Should_Fail_Creating_More_Credit_Cards() {
        testRestTemplate.postForEntity("/api/cards/{customerId}/create/credit-card", null, ResponseDto.class, 16L);
        testRestTemplate.postForEntity("/api/cards/{customerId}/create/credit-card", null, ResponseDto.class, 16L);
        testRestTemplate.postForEntity("/api/cards/{customerId}/create/credit-card", null, ResponseDto.class, 16L);
        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity("/api/cards/{customerId}/create/credit-card", null, ErrorResponseDto.class, 16L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/cards/16/create/credit-card");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Creation limit reached. You can only have a maximum of 3 Credit Cards.");
    }

    @Test
    void Should_Succeed_Fetching_Cards_Details() {
        Map<String, Long> uriVariables = new HashMap<>();
        uriVariables.put("customerId", 10L);
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/cards/{customerId}/fetch").buildAndExpand(uriVariables).toUri();

        createdCard = cardService.createDebitCard(10L);

        ResponseEntity<List<CardDto>> response = testRestTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<CardDto>>(){});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get(0).getCardNumber()).isNotBlank();
        assertThat(response.getBody().get(0).getCardType()).isEqualTo("Debit Card");
        assertThat(response.getBody().get(0).getCustomerId()).isEqualTo(10L);
    }

    @Test
    void Should_Fail_Fetching_Cards_Details() {
        Map<String, Long> uriVariables = new HashMap<>();
        uriVariables.put("customerId", null);
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/cards/{customerId}/fetch").buildAndExpand(uriVariables).toUri();

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.GET, null, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/cards//fetch");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("No static resource api/cards/fetch.");
    }

    @Test
    void Should_Succeed_Updating_Card_Purchase_Limit() {
        // add card to db and get the created card inside a variable
        createdCard = cardService.createDebitCard(10L);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cardNumber", createdCard.getCardNumber());
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/cards/update/{cardNumber}").buildAndExpand(uriVariables).toUri();

        createdCard.setMonthlyPurchaseLimit(15000);

        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(createdCard), ResponseDto.class);

        Card updatedCard = cardRepository.findByCardNumber(createdCard.getCardNumber()).get();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Request processed successfully");
        assertThat(updatedCard.getMonthlyPurchaseLimit()).isEqualTo(15000);
    }

    @Test
    void Should_Fail_Updating_Card_Purchase_Limit() {
        // add card to db and get the created card inside a variable
        createdCard = cardService.createDebitCard(10L);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cardNumber", createdCard.getCardNumber());
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/cards/update/{cardNumber}").buildAndExpand(uriVariables).toUri();

        createdCard.setMonthlyPurchaseLimit(150000);

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(createdCard), ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorMessage()).isEqualTo("[monthlyPurchaseLimit: Monthly purchase limit should be less than or equal to 100000]");
    }

    @Test
    void Should_Succeed_Deleting_Card() {
        // add card to db and get the created card inside a variable
        createdCard = cardService.createDebitCard(10L);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cardNumber", createdCard.getCardNumber());
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/api/cards/delete/{cardNumber}")
                .buildAndExpand(uriVariables).toUri();

        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.DELETE, null, ResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Request processed successfully");
    }

    @Test
    void Should_Fail_Deleting_Card() {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cardNumber", "100234567891");
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/api/cards/delete/{cardNumber}")
                .buildAndExpand(uriVariables).toUri();

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.DELETE, null,  ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/cards/delete/100234567891");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Card not found with the given card number");
    }

    @Test
    void Should_Succeed_Deleting_All_Cards() {
        // add card to db and get the created card inside a variable
        cardService.createDebitCard(10L);
        cardService.createDebitCard(10L);
        cardService.createCreditCard(10L);

        Map<String, Long> uriVariables = new HashMap<>();
        uriVariables.put("customerId", 10L);
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/api/cards/{customerId}/delete")
                .buildAndExpand(uriVariables).toUri();

        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.DELETE, null, ResponseDto.class);

        List<Card> cardsList = cardRepository.findAllByCustomerId(10L).get();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Request processed successfully");
        assertThat(cardsList.isEmpty()).isTrue();
    }

    @Test
    void Should_Fail_Deleting_All_Cards() {
        Map<String, Long> uriVariables = new HashMap<>();
        uriVariables.put("customerId", 9L);
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/api/cards/{customerId}/delete")
                .buildAndExpand(uriVariables).toUri();

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.DELETE, null, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/cards/9/delete");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Cards not found with the given customer id");
    }
}
