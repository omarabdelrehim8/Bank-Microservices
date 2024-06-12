package com.omarabdelrehim8.accounts.service.messagebroker;

import com.omarabdelrehim8.accounts.dto.AccountsMsgDto;
import com.omarabdelrehim8.accounts.entity.Account;
import com.omarabdelrehim8.accounts.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BrokerCommunication {

    private final StreamBridge streamBridge;

    public void sendCommunication(Account account, Customer customer) {
        AccountsMsgDto accountsMsgDto = new AccountsMsgDto(
                account.getAccountNumber(),
                customer.getName(),
                customer.getEmail(),
                customer.getMobileNumber());

        log.info("Sending BrokerCommunication request for the details: {}", accountsMsgDto);
        boolean result = streamBridge.send("sendCommunication-out-0", accountsMsgDto);
        log.info("Is the BrokerCommunication request successfully triggered?: {}", result);
    }
}
