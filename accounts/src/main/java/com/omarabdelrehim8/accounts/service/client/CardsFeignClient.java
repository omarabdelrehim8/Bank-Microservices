package com.omarabdelrehim8.accounts.service.client;

import com.omarabdelrehim8.accounts.dto.CardDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "cards", fallbackFactory = CardsFallbackFactory.class)
public interface CardsFeignClient {

    @GetMapping(value = "/api/fetch", consumes = "application/json")
    ResponseEntity<List<CardDto>> fetchCardsDetails (@RequestParam Long customerId);
}
