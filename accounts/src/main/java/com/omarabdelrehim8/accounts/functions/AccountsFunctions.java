package com.omarabdelrehim8.accounts.functions;

import com.omarabdelrehim8.accounts.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class AccountsFunctions {

    @Bean
    public Consumer<Long> updateCommunication(AccountService accountService) {
        return accountNumber -> {
            log.info("Updating BrokerCommunication status for the account number: {}", accountNumber.toString());

            accountService.updateCommunicationStatus(accountNumber);
        };
    }
}
