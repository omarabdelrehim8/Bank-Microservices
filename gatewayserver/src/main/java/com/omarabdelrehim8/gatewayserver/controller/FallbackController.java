package com.omarabdelrehim8.gatewayserver.controller;

import com.omarabdelrehim8.gatewayserver.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
public class FallbackController {

    @RequestMapping("/contactSupport")
    public Mono<ResponseEntity<ErrorResponseDto>> contactSupport(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(
                        exchange.getRequest().getPath().toString(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An error occurred. " +
                                "Please try again later or contact customer service!",
                        LocalDateTime.now())));
    }
}
