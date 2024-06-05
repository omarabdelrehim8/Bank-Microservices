package com.omarabdelrehim8.cards.service.client;

import com.omarabdelrehim8.cards.dto.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "accounts")
public interface AccountsFeignClient {

    @GetMapping(value = "/api/fetch-details", consumes = "application/json")
    ResponseEntity<List<AccountDto>> fetchAccountsDetails(@RequestParam Long customerId);
}
