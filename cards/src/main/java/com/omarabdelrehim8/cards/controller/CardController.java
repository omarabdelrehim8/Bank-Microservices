package com.omarabdelrehim8.cards.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.omarabdelrehim8.cards.dto.*;
import com.omarabdelrehim8.cards.dto.constraints.CardValidation;
import com.omarabdelrehim8.cards.dto.constraints.CardView;
import com.omarabdelrehim8.cards.service.CardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.omarabdelrehim8.cards.constants.CardConstants.*;

@RestController
@RequestMapping(path = "/api/cards", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class CardController {

    private final CardService cardService;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private ServiceContactInfoDto serviceContactInfoDto;

    @PostMapping("/create/debit-card")
    public ResponseEntity<ResponseDto> createDebitCard (@Valid @RequestBody CardCreationRequestDto creationRequest) {

        CardDto cardDto = cardService.createDebitCard(creationRequest.getCustomerId(), Long.parseLong(creationRequest.getAccountNumber()));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.value(), MESSAGE_201, cardDto));
    }

    @PostMapping("/create/credit-card")
    public ResponseEntity<ResponseDto> createCreditCard (@Valid @RequestBody CardCreationRequestDto creationRequest) {

        CardDto cardDto = cardService.createCreditCard(creationRequest.getCustomerId(), Long.parseLong(creationRequest.getAccountNumber()));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.value(), MESSAGE_201, cardDto));
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<CardDto>> fetchCardsDetails (@RequestParam
                                                            @NotNull(message = "Customer ID is required")
                                                            @Min(value = 1, message = "Customer ID must be bigger than 0")
                                                            Long customerId) {

        List<CardDto> cardsDtoList = cardService.fetchCards(customerId);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(cardsDtoList);
    }

    @PutMapping("/{cardNumber}/update")
    @JsonView(CardView.OnUpdate.class)
    public ResponseEntity<?> updateCardPurchaseLimit (HttpServletRequest request,

                                                   @PathVariable
                                                   @NotBlank(message = "Card number is required")
                                                   @Pattern(regexp="(^$|[0-9]{12})",message = "Card number must be 12 digits")
                                                   String cardNumber,

                                                   @RequestBody
                                                   @JsonView(CardView.OnUpdate.class)
                                                   @Validated(CardValidation.OnUpdate.class)
                                                   CardDto cardDto) {

        boolean isUpdated = cardService.updateCardMonthlyPurchaseLimit(cardNumber, cardDto.getMonthlyPurchaseLimit());

        if(isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(HttpStatus.OK.value(), MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ErrorResponseDto(request.getRequestURI(),
                                               HttpStatus.EXPECTATION_FAILED,
                                               MESSAGE_417_UPDATE,
                                               LocalDateTime.now()));
        }
    }

    @DeleteMapping("/{cardNumber}/delete")
    @JsonView(CardView.OnDelete.class)
    public ResponseEntity<?> deleteCard (HttpServletRequest request, @PathVariable
                                                                     @NotBlank(message = "Card number is required")
                                                                     @Pattern(regexp="(^$|[0-9]{12})",message = "Card number must be 12 digits")
                                                                     String cardNumber) {
        boolean isDeleted = cardService.deleteCard(cardNumber);

        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(HttpStatus.OK.value(), MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ErrorResponseDto(request.getRequestURI(),
                                               HttpStatus.EXPECTATION_FAILED,
                                               MESSAGE_417_DELETE,
                                               LocalDateTime.now()));
        }
    }

    @DeleteMapping("/delete")
    @JsonView(CardView.OnDelete.class)
    public ResponseEntity<?> deleteAllCards (HttpServletRequest request, @RequestParam
                                                                         @NotNull(message = "Customer ID is required")
                                                                         @Min(value = 1, message = "Customer ID must be bigger than 0")
                                                                         Long customerId) {
        boolean isDeleted = cardService.deleteAllCards(customerId);

        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(HttpStatus.OK.value(), MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ErrorResponseDto(request.getRequestURI(),
                            HttpStatus.EXPECTATION_FAILED,
                            MESSAGE_417_DELETE,
                            LocalDateTime.now()));
        }
    }

    @GetMapping("/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(buildVersion);
    }

    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }

    @GetMapping("/contact-info")
    public ResponseEntity<ServiceContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(serviceContactInfoDto);
    }
}
