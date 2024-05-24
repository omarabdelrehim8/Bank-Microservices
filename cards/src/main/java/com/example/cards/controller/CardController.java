package com.example.cards.controller;

import com.example.cards.dto.CardDto;
import com.example.cards.dto.ErrorResponseDto;
import com.example.cards.dto.ResponseDto;
import com.example.cards.dto.constraints.CardValidation;
import com.example.cards.dto.constraints.CardView;
import com.example.cards.service.CardService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.cards.constants.CardConstants.*;

@RestController
@RequestMapping(path = "/api/cards", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class CardController {

    private final CardService cardService;

    @PostMapping("/{customerId}/create/debit-card")
    public ResponseEntity<ResponseDto> createDebitCard (@PathVariable
                                                        @NotNull(message = "Customer ID is required")
                                                        @Min(value = 1, message = "Customer ID must be bigger than 0")
                                                        Long customerId) {

        CardDto cardDto = cardService.createDebitCard(customerId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.value(), MESSAGE_201, cardDto));
    }

    @PostMapping("/{customerId}/create/credit-card")
    public ResponseEntity<ResponseDto> createCreditCard (@PathVariable
                                                         @NotNull(message = "Customer ID is required")
                                                         @Min(value = 1, message = "Customer ID must be bigger than 0")
                                                         Long customerId) {

        CardDto cardDto = cardService.createCreditCard(customerId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.value(), MESSAGE_201, cardDto));
    }

    @GetMapping("/{customerId}/fetch")
    public ResponseEntity<List<CardDto>> fetchCardsDetails (@PathVariable
                                                            @NotNull(message = "Customer ID is required")
                                                            @Min(value = 1, message = "Customer ID must be bigger than 0")
                                                            Long customerId) {

        List<CardDto> cardsDtoList = cardService.fetchCards(customerId);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(cardsDtoList);
    }

    @PutMapping("/update/{cardNumber}")
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

    @DeleteMapping("/delete/{cardNumber}")
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

    @DeleteMapping("/{customerId}/delete")
    @JsonView(CardView.OnDelete.class)
    public ResponseEntity<?> deleteAllCards (HttpServletRequest request, @PathVariable
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
}
