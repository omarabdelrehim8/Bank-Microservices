package com.omarabdelrehim8.cards.controller;

import com.omarabdelrehim8.cards.dto.CardCreationRequestDto;
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

@Disabled("Needs feign client mock")
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
        creationRequest = CardCreationRequestDto.builder()
                                                .customerId(1L)
                                                .accountNumber("1076368322")
                                                .build();

        ResponseEntity<ResponseDto> response = testRestTemplate.postForEntity("/api/create/debit-card", creationRequest, ResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatusCode()).isEqualTo(201);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Card created successfully");
        assertThat(response.getBody().getCard()).extracting("customerId", "cardType").containsExactly(1L, "Debit Card");
    }

    @Test
    void Should_Fail_Creating_More_Debit_Cards() {
        creationRequest = CardCreationRequestDto.builder()
                                                .customerId(1L)
                                                .accountNumber("1076368322")
                                                .build();

        testRestTemplate.postForEntity("/api/create/debit-card", creationRequest, ResponseDto.class);
        testRestTemplate.postForEntity("/api/create/debit-card", creationRequest, ResponseDto.class);
        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity("/api/create/debit-card", creationRequest, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/create/debit-card");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Creation limit reached. You can only have a maximum of 2 Debit Cards per account.");
    }

    @Test
    void Should_Fail_Creating_Debit_Card_When_Account_Not_Found() {
        creationRequest = CardCreationRequestDto.builder()
                                                .customerId(1L)
                                                .accountNumber("1076368310")
                                                .build();

        testRestTemplate.postForEntity("/api/create/debit-card", creationRequest, ResponseDto.class);
        testRestTemplate.postForEntity("/api/create/debit-card", creationRequest, ResponseDto.class);
        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity("/api/create/debit-card", creationRequest, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/create/debit-card");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Account number: 1076368310 is not associated to the given customer id. Please give a valid account number and try again.");
    }

    @Test
    void Should_Succeed_Creating_Credit_Card() {
        creationRequest = CardCreationRequestDto.builder()
                                                .customerId(1L)
                                                .accountNumber("1076368322")
                                                .build();

        ResponseEntity<ResponseDto> response = testRestTemplate.postForEntity("/api/create/credit-card", creationRequest, ResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatusCode()).isEqualTo(201);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Card created successfully");
        assertThat(response.getBody().getCard()).extracting("customerId", "cardType").containsExactly(1L, "Credit Card");
    }

    @Test
    void Should_Fail_Creating_More_Credit_Cards() {
        creationRequest = CardCreationRequestDto.builder()
                                                .customerId(1L)
                                                .accountNumber("1076368322")
                                                .build();

        testRestTemplate.postForEntity("/api/create/credit-card", creationRequest, ResponseDto.class);
        testRestTemplate.postForEntity("/api/create/credit-card", creationRequest, ResponseDto.class);
        testRestTemplate.postForEntity("/api/create/credit-card", creationRequest, ResponseDto.class);
        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity("/api/create/credit-card", creationRequest, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/create/credit-card");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Creation limit reached. You can only have a maximum of 3 Credit Cards per account.");
    }

    @Test
    void Should_Fail_Creating_Credit_Card_When_Account_Not_Found() {
        creationRequest = CardCreationRequestDto.builder()
                                                .customerId(1L)
                                                .accountNumber("1076368310")
                                                .build();

        testRestTemplate.postForEntity("/api/create/credit-card", creationRequest, ResponseDto.class);
        testRestTemplate.postForEntity("/api/create/credit-card", creationRequest, ResponseDto.class);
        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity("/api/create/credit-card", creationRequest, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/create/credit-card");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Account number: 1076368310 is not associated to the given customer id. Please give a valid account number and try again.");
    }

    @Test
    void Should_Succeed_Fetching_Cards_Details() {
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/fetch").queryParam("customerId", 1).build().toUri();

        createdCard = cardService.createDebitCard(1L, 1076368322L);

        ResponseEntity<List<CardDto>> response = testRestTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<CardDto>>(){});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get(0).getCardNumber()).isNotBlank();
        assertThat(response.getBody().get(0).getCardType()).isEqualTo("Debit Card");
        assertThat(response.getBody().get(0).getCustomerId()).isEqualTo(1L);
    }

    @Test
    void Should_Fail_Fetching_Cards_Details() {
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/fetch").queryParam("customerId", 10).build().toUri();

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.GET, null, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/fetch");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Cards not found with the given customer id");
    }

    @Test
    void Should_Succeed_Updating_Card_Purchase_Limit() {
        // add card to db and get the created card inside a variable
        createdCard = cardService.createDebitCard(1L, 1076368322L);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cardNumber", createdCard.getCardNumber());
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/{cardNumber}/update").buildAndExpand(uriVariables).toUri();

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
        createdCard = cardService.createDebitCard(1L, 1076368322L);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cardNumber", createdCard.getCardNumber());
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api/{cardNumber}/update").buildAndExpand(uriVariables).toUri();

        createdCard.setMonthlyPurchaseLimit(150000);

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(createdCard), ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorMessage()).isEqualTo("[monthlyPurchaseLimit: Monthly purchase limit should be less than or equal to 100000]");
    }

    @Test
    void Should_Succeed_Deleting_Card() {
        // add card to db and get the created card inside a variable
        createdCard = cardService.createDebitCard(1L, 1076368322L);

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cardNumber", createdCard.getCardNumber());
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/api/{cardNumber}/delete")
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
                .path("/api/{cardNumber}/delete")
                .buildAndExpand(uriVariables).toUri();

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.DELETE, null,  ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/100234567891/delete");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Card not found with the given card number");
    }

    @Test
    void Should_Succeed_Deleting_All_Cards() {
        // add card to db and get the created card inside a variable
        cardService.createDebitCard(1L, 1076368322L);
        cardService.createDebitCard(1L, 1076368322L);
        cardService.createCreditCard(1L, 1076368322L);

        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/api/delete")
                .queryParam("customerId", 1L).build().toUri();

        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.DELETE, null, ResponseDto.class);

        List<Card> cardsList = cardRepository.findAllByCustomerId(1L).get();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().getStatusMessage()).isEqualTo("Request processed successfully");
        assertThat(cardsList.isEmpty()).isTrue();
    }

    @Test
    void Should_Fail_Deleting_All_Cards() {
        String url = "http://localhost:" + this.port;
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/api/delete")
                .queryParam("customerId", 1L).build().toUri();

        ResponseEntity<ErrorResponseDto> response = testRestTemplate.exchange(uri, HttpMethod.DELETE, null, ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getApiPath()).isEqualTo("/api/delete");
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Cards not found with the given customer id");
    }
}
